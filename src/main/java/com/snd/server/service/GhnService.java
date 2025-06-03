package com.snd.server.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GhnService {
    private final RestTemplate restTemplate;

    public GhnService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${ghn.token}")
    private String ghnToken;
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Map<String, Object>> getDistricts() {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/master-data/district";
        HttpHeaders headers = defaultHeaders();

        Map<String, Object> body = Collections.emptyMap();
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        return extractDataList(response.getBody());
    }

    public List<Map<String, Object>> getWards(int districtId) {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/master-data/ward";
        HttpHeaders headers = defaultHeaders();

        Map<String, Integer> body = Map.of("district_id", districtId);
        HttpEntity<Map<String, Integer>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        return extractDataList(response.getBody());
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private List<Map<String, Object>> extractDataList(String responseBody) {
        try {
            JsonNode root = mapper.readTree(responseBody);
            return mapper.convertValue(root.path("data"), new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
