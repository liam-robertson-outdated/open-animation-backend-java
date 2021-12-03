package com.openanimationbackend.animation;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.URISyntaxException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequestMapping(path = "animation")
public class AnimationService {

    AnimationPrimaryController animationPrimaryController;

    public AnimationService(AnimationPrimaryController animationPrimaryController) {
        this.animationPrimaryController = animationPrimaryController;
    }

    @GetMapping("getFullAnimation")
    public void getFullAnimation() throws Exception {
        animationPrimaryController.primaryExecutor();
    }

}
