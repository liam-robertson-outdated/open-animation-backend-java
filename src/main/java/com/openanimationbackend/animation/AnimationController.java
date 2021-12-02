package com.openanimationbackend.animation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "animation")
public class AnimationController {

    private AnimationService animationService;

    public AnimationController(AnimationService animationService) {
        this.animationService = animationService;
    }

    @GetMapping("getFullAnimation")
    public void getFullAnimation() throws IOException {

    }
}
