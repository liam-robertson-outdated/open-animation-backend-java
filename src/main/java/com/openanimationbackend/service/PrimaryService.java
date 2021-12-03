package com.openanimationbackend.service;

import com.openanimationbackend.controller.PrimaryController;
import com.openanimationbackend.controller.UploadController;
import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequestMapping(path = "animation")
public class PrimaryService {

    PrimaryController primaryController;

    public PrimaryService(PrimaryController primaryController, UploadController uploadControllerController) {
        this.primaryController = primaryController;
    }

    @GetMapping("getFullAnimation")
    public void updateFinalVideo() throws Exception {
    }

}
