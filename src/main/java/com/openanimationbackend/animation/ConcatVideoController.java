package com.openanimationbackend.animation;

import org.mp4parser.Container;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.container.mp4.MovieCreator;
import org.mp4parser.muxer.tracks.AppendTrack;
import org.mp4parser.muxer.tracks.ClippedTrack;
import org.springframework.core.io.ClassPathResource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;

public class ConcatVideoController {

    String[] videoUris = new String[]{
            new ClassPathResource("animation-videos/video1.mp4", this.getClass().getClassLoader()).getFile().toString(),
            new ClassPathResource("animation-videos/video1.mp4", this.getClass().getClassLoader()).getFile().toString(),
    };

    String[] audioUris = new String[]{
            new ClassPathResource("static/harry-potter_philosophers-stone_chapter-1.mp4", this.getClass().getClassLoader()).getFile().toString(),
    };

    public ConcatVideoController(String[] videoUris, String[] audioUris) throws IOException {
        this.videoUris = videoUris;
        this.audioUris = audioUris;
    }

    public void concatAnimations() throws IOException {
        String audioTrackPath = "D:\\Users\\liam\\OneDrive\\programming\\open-animation\\open-animation-backend\\target\\classes\\static\\harry-potter_philosophers-stone_chapter-1.mp4";
        Movie movie = new Movie();
        Movie audioMp4 = MovieCreator.build(audioTrackPath);
        Track audioTrack = audioMp4.getTracks().get(0);
        movie.addTrack(audioTrack);
        Container out = new DefaultMp4Builder().build(movie);
        FileChannel fc = new RandomAccessFile(String.format("output.mp4"), "rw").getChannel();
        out.writeContainer(fc);
        fc.close();
    }

    public void trimAudio() throws IOException {
        Movie movie = MovieCreator.build("D:\\Users\\liam\\OneDrive\\programming\\open-animation\\open-animation-backend\\target\\classes\\static\\harry-potter_philosophers-stone_chapter-1.mp4");
        Track audioTrack = movie.getTracks().get(0);
        movie.setTracks(new LinkedList<Track>());

        double startTime = 10;
        double endTime = 20;
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
        movie.addTrack(new AppendTrack(new ClippedTrack(audioTrack, startSample, endSample)));

        Container out = new DefaultMp4Builder().build(movie);
        FileOutputStream fos = new FileOutputStream("output.mp4");
        FileChannel fc = fos.getChannel();
        out.writeContainer(fc);

        fc.close();
        fos.close();
    }


    private double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                // samples always start with 1 but we start with zero therefore +1
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
