package com.campusskill.controller;

import com.campusskill.dto.PublicPreviewResponse;
import com.campusskill.service.PreviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final PreviewService previewService;

    public PublicController(PreviewService previewService) {
        this.previewService = previewService;
    }

    @GetMapping("/preview")
    public PublicPreviewResponse preview() {
        return previewService.getPreview();
    }
}
