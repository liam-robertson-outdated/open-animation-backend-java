package com.openanimationbackend;

import org.springframework.web.bind.annotation.RestController;

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

    protected void updateFinalVideo() throws Exception {
        videoStitchingController.trimAudio();
        videoStitchingController.joinVideosAndAudio();
    }

}
