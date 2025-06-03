package com.snd.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate;

    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${map.token}")
    private String OPENCAGE_API_KEY;

    public Coordinates getCoordinatesFromAddress(String address) {
        try {
            // String encodedAddress = UriUtils.encodePath(address, StandardCharsets.UTF_8);
            String url = String.format(
                    "https://api.opencagedata.com/geocode/v1/json?q=%s&key=%s&language=vi&pretty=1",
                    address, OPENCAGE_API_KEY);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode results = root.path("results");

            if (results.isArray() && results.size() > 0) {
                JsonNode geometry = results.get(0).path("geometry");
                double lat = geometry.get("lat").asDouble();
                double lng = geometry.get("lng").asDouble();
                return new Coordinates(lat, lng);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getAddressFromCoordinates(double latitude, double longitude) {
        try {
            String url = String.format(
                    "https://api.opencagedata.com/geocode/v1/json?q=%f+%f&key=%s&language=vi&pretty=1",
                    latitude, longitude, OPENCAGE_API_KEY);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode results = root.path("results");

            if (results.isArray() && results.size() > 0) {
                JsonNode formatted = results.get(0).path("formatted");
                return formatted.asText();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Data
    @AllArgsConstructor
    public static class Coordinates {
        private double latitude;
        private double longitude;
    }
}
