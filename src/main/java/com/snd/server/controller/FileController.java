package com.snd.server.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.snd.server.service.FileService;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/file/upload-image", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile image)
            throws IOException {
        String imageUrl = fileService.uploadImage(image);
        Map<String, String> response = new HashMap<>();
        response.put("data", imageUrl);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/file/upload-images", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, List<String>>> uploadImages(@RequestParam("files") MultipartFile[] image)
            throws IOException {
        List<String> imageUrls = fileService.uploadMultipleImages(image);
        Map<String, List<String>> response = new HashMap<>();
        response.put("data", imageUrls);
        return ResponseEntity.ok(response);
    }

}
