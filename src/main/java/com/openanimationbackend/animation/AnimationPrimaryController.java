package com.openanimationbackend.animation;

import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AnimationPrimaryController {

    ConcatVideoController concatVideoController;

    public AnimationPrimaryController(ConcatVideoController concatVideoController) {
        this.concatVideoController = concatVideoController;
    }

    /**
     * Takes in animation videos from resources/animation-videos
     * Takes in audio from resources/static/audio
     * Trims audio to be 60 seconds longer than the combined length of the video files
     * Joins the video files together in order with the audio track over them
     * Outputs the combined file to resources/output
     *
     */
    protected void primaryExecutor() throws Exception {
        concatVideoController.trimAudio();
        concatVideoController.joinVideosAndAudio();
    }




}
