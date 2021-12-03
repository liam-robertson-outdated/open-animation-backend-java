package com.openanimationbackend.animation;

import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AnimationPrimaryController {

    ConcatVideoController concatVideoController;

    public AnimationPrimaryController(ConcatVideoController concatVideoController) {
        this.concatVideoController = concatVideoController;
    }

    protected void primaryExecutor() throws IOException {
        concatVideoController.concatAnimations();
    }




}
