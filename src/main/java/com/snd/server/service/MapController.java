package com.snd.server.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/public/map")
@RequiredArgsConstructor
public class MapController {

    private final GeocodingService geocodingService;

    @GetMapping("/geocode")
    public ResponseEntity<?> getCoordinates(@RequestParam String address) {
        GeocodingService.Coordinates coordinates = geocodingService.getCoordinatesFromAddress(address);
        if (coordinates == null) {
            return ResponseEntity.badRequest().body("Không thể tìm thấy tọa độ");
        }
        return ResponseEntity.ok(coordinates);
    }

    @GetMapping("/reverse")
    public ResponseEntity<?> getAddressFromCoords(@RequestParam double lat, @RequestParam double lng) {
        String address = geocodingService.getAddressFromCoordinates(lat, lng);
        if (address == null) {
            return ResponseEntity.badRequest().body("Không thể tìm thấy địa chỉ.");
        }
        return ResponseEntity.ok(address);
    }

}
