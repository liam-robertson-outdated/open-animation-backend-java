package com.openanimationbackend.service;

import com.openanimationbackend.controller.PrimaryController;
import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.channels.FileChannel;

@Service
@RequestMapping(path = "home")
public class PrimaryService {

    private PrimaryController primaryController;

    public PrimaryService(PrimaryController primaryController) {
        this.primaryController = primaryController;
    }
}
