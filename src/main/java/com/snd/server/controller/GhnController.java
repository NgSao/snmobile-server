package com.snd.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.snd.server.service.GhnService;

@RestController
@RequestMapping("/api/v1/public/ghn")
public class GhnController {

    private final GhnService ghnService;

    public GhnController(GhnService ghnService) {
        this.ghnService = ghnService;
    }

    @GetMapping("/districts")
    public ResponseEntity<?> getDistricts() {
        return ResponseEntity.ok(ghnService.getDistricts());
    }

    @GetMapping("/wards")
    public ResponseEntity<?> getWards(@RequestParam int districtId) {
        return ResponseEntity.ok(ghnService.getWards(districtId));
    }
}
