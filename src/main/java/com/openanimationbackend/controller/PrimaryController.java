package com.openanimationbackend.controller;

import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.TrackBox;
import org.mp4parser.boxes.iso14496.part15.AvcConfigurationBox;
import org.mp4parser.muxer.FileRandomAccessSourceImpl;
import org.mp4parser.muxer.Sample;
import org.mp4parser.muxer.container.mp4.Mp4SampleList;
import org.mp4parser.tools.IsoTypeReaderVariable;
import org.mp4parser.tools.Path;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

@RestController
public class PrimaryController {

    VideoStitchingController videoStitchingController;
    UploadController uploadController;

    public PrimaryController(VideoStitchingController videoStitchingController, UploadController uploadController) {
        this.videoStitchingController = videoStitchingController;
        this.uploadController = uploadController;
    }

    /**
     * Takes in video from user and places it in resources/animation-videos
     * Takes in audio track from resources/static/audio
     * Trims audio to be 60 seconds longer than the final video file
     * Stitches the video files together and puts the audio track on top of them
     * Outputs the final video to resources/output
     */
    public void addNewAnimation() throws Exception {
        uploadController.addNewAnimation();
        this.updateFinalVideo();
    }

    public void updateFinalVideo() throws Exception {
        videoStitchingController.trimAudio();
        videoStitchingController.joinVideosAndAudio();
    }

}
