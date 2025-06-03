package com.snd.server.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.snd.server.model.Session;
import com.snd.server.repository.SessionRepository;

@Service
public class TokenBlacklistService {

    private final SessionRepository sessionRepository;

    public TokenBlacklistService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public boolean isBlacklisted(String token) {
        Optional<Session> sessionOpt = sessionRepository.findByPayload(token);
        return sessionOpt.isEmpty() || !sessionOpt.get().isFlag();
    }

    public void logout(String token) {
        sessionRepository.findByPayload(token).ifPresent(session -> {
            session.setFlag(false);
            sessionRepository.save(session);
        });
    }
}
