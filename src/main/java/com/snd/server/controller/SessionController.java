package com.snd.server.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.snd.server.constant.ApiPathConstant;
import com.snd.server.dto.request.SessionRequest;
import com.snd.server.dto.response.SessionResponse;
import com.snd.server.service.SessionService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/session")
    public ResponseEntity<List<SessionResponse>> getSessionsByPayload() {
        return ResponseEntity.ok().body(sessionService.getSessionsByPayload());
    }

    @PutMapping("/session/login")
    public ResponseEntity<Boolean> updatedSessions(@RequestBody SessionRequest request) {
        sessionService.updatedSessions(request);
        return ResponseEntity.ok().body(true);
    }

}
