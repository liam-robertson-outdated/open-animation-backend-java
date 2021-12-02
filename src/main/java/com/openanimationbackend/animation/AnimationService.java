package com.openanimationbackend.animation;

import org.mp4parser.muxer.tracks.ClippedTrack;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.mp4parser.Container;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.container.mp4.MovieCreator;
import org.mp4parser.muxer.tracks.AppendTrack;

@Service
public class AnimationService {

    String[] videoUris = new String[]{
            new ClassPathResource("animation-videos/video1.mp4", this.getClass().getClassLoader()).getFile().toString(),
            new ClassPathResource("animation-videos/video1.mp4", this.getClass().getClassLoader()).getFile().toString(),
//            new ClassPathResource("static/harry-potter_philosophers-stone_chapter-1.mp3", this.getClass().getClassLoader()).getFile().toString(),
    };

    public AnimationService() throws IOException {
    }

    public void concatAnimations() throws IOException, URISyntaxException {
        String audioEnglish = new ClassPathResource("static/harry-potter_philosophers-stone_chapter-1.mp4", this.getClass().getClassLoader()).getFile().toString();
        Movie countAudioEnglish = MovieCreator.build(audioEnglish);

        List<Movie> inMovies = new ArrayList<Movie>();
        for (String videoUri : videoUris) {
            inMovies.add(MovieCreator.build(videoUri));
        }
        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();

        for (Movie m : inMovies) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
                for (int i = 0; i < t.getSampleDurations().length; i++) {
                    double currentTime = 0;
                    long delta = t.getSampleDurations()[i];
                    currentTime += (double) delta / (double) t.getTrackMetaData().getTimescale();

                }
            }
        }

        Movie result = new Movie();
        Track audioTrackEnglish = countAudioEnglish.getTracks().get(0);
        result.addTrack(audioTrackEnglish);
        result.addTrack(new AppendTrack(new ClippedTrack(track, startSample1, endSample1)));
//        https://github.com/sannies/mp4parser/blob/master/examples/src/main/java/com/googlecode/mp4parser/SimpleShortenExample.java//        result.addTrack(audioTrackEnglish);

//        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));

//        if (!audioTracks.isEmpty()) {
//            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
//        }
        if (!videoTracks.isEmpty()) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        Container out = new DefaultMp4Builder().build(result);

        FileChannel fc = new RandomAccessFile(String.format("output.mp4"), "rw").getChannel();
        out.writeContainer(fc);
        fc.close();

    }


    public void getFullAnimation(String directory) throws IOException {

    }

}
