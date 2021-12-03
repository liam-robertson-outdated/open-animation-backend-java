package com.openanimationbackend;

import org.mp4parser.Container;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.container.mp4.MovieCreator;
import org.mp4parser.muxer.tracks.AppendTrack;
import org.mp4parser.muxer.tracks.ClippedTrack;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@RestController
public class VideoStitchingController {

    protected void joinVideosAndAudio() throws Exception {
        List<Movie> inMovies = new ArrayList<>();
        List<Track> videoTracks = new LinkedList<>();
        Movie finalMovie = new Movie();

        this.addVideoTracksFromResources(inMovies, videoTracks, finalMovie);
        this.addAudioTrackFromResources(finalMovie);
        this.writeFinalMovieTocontainer(finalMovie);
    }

    protected void trimAudio() throws Exception {
        double totalVideoLength = this.getTotalVideoLength();
        double audioTrackLength = totalVideoLength + 60;
        String audioMp4Path = new ClassPathResource("static/audiotrack.mp4", this.getClass().getClassLoader()).getFile().toString();
        Movie audioMovie = MovieCreator.build(audioMp4Path);
        Track audioTrack = audioMovie.getTracks().get(0);
        audioMovie.setTracks(new LinkedList<Track>());

        double startTime = 0;
        double endTime = audioTrackLength;
        boolean timeCorrected = false;

        if (audioTrack.getSyncSamples() != null && audioTrack.getSyncSamples().length > 0) {
            if (timeCorrected) {
                throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
            }
            startTime = this.correctTimeToSyncSample(audioTrack, startTime, false);
            endTime = this.correctTimeToSyncSample(audioTrack, endTime, true);
            timeCorrected = true;
        }

        long currentSample = 0;
        double currentTime = 0;
        double lastTime = -1;
        long startSample = -1;
        long endSample = -1;

        for (int i = 0; i < audioTrack.getSampleDurations().length; i++) {
            long delta = audioTrack.getSampleDurations()[i];

            if (currentTime > lastTime && currentTime <= startTime) {
                startSample = currentSample;
            }
            if (currentTime > lastTime && currentTime <= endTime) {
                endSample = currentSample;
            }
            lastTime = currentTime;
            currentTime += (double) delta / (double) audioTrack.getTrackMetaData().getTimescale();
            currentSample++;
        }
        audioMovie.addTrack(new AppendTrack(new ClippedTrack(audioTrack, startSample, endSample)));

        Container out = new DefaultMp4Builder().build(audioMovie);
        FileOutputStream fos = new FileOutputStream("src/main/resources/trimmed-audiotracks/trimmed-audiotrack.mp4");
        FileChannel fc = fos.getChannel();
        out.writeContainer(fc);

        fc.close();
        fos.close();
    }

    private void writeFinalMovieTocontainer(Movie finalMovie) throws IOException {
        Container out = new DefaultMp4Builder().build(finalMovie);
        FileChannel fc = new RandomAccessFile(String.format("src/main/resources/output/final-video.mp4"), "rw").getChannel();
        out.writeContainer(fc);
        fc.close();
    }

    private void addAudioTrackFromResources(Movie finalMovie) throws IOException {
        String audioTrackPath = new ClassPathResource("trimmed-audiotracks/trimmed-audiotrack.mp4", this.getClass().getClassLoader()).getFile().toString();
        Movie audioMp4 = MovieCreator.build(audioTrackPath);
        Track audioTrack = audioMp4.getTracks().get(0);
        finalMovie.addTrack(audioTrack);
    }

    public double getTotalVideoLength() throws Exception {
        File videoFolderPath = new ClassPathResource("animation-videos", this.getClass().getClassLoader()).getFile();
        double totalVideoLength = 0;
        for (File file : videoFolderPath.listFiles()) {
            IsoFile isoFile = new IsoFile(file);
            MovieHeaderBox mhb = isoFile.getMovieBox().getMovieHeaderBox();
            totalVideoLength += mhb.getDuration() / mhb.getTimescale();
        }
        return totalVideoLength;
    }

    private void addVideoTracksFromResources(List<Movie> inMovies, List<Track> videoTracks, Movie finalMovie) throws IOException {
        File videoFolderPath = new ClassPathResource("animation-videos", this.getClass().getClassLoader()).getFile();
        for (File file : videoFolderPath.listFiles()) {
            inMovies.add(MovieCreator.build(file.toString()));
        }

        for (Movie m : inMovies) {
            for (Track track : m.getTracks()) {
                if (track.getHandler().equals("vide")) {
                    videoTracks.add(track);
                }
            }
        }
        finalMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
    }

    private double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }
}
