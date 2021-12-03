package com.openanimationbackend;

import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequestMapping(path = "animation")
public class AnimationService {

    PrimaryController primaryController;

    public AnimationService(PrimaryController primaryController, UploadController uploadControllerController) {
        this.primaryController = primaryController;
    }

    @GetMapping("getFullAnimation")
    public void updateFinalVideo() throws Exception {
        primaryController.getFullAnimation();
    }

}
