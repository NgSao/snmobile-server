package com.snd.server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.snd.server.constant.MonoConstant;
import com.snd.server.dto.request.SessionRequest;
import com.snd.server.dto.response.SessionResponse;
import com.snd.server.exception.AppException;
import com.snd.server.mapper.UserMapper;
import com.snd.server.model.Session;
import com.snd.server.repository.SessionRepository;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final UserMapper userMapper;

    public SessionService(SessionRepository sessionRepository, UserMapper userMapper) {
        this.sessionRepository = sessionRepository;
        this.userMapper = userMapper;
    }

    public List<SessionResponse> getSessionsByPayload() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(MonoConstant.UUID);
        List<Session> sessions = sessionRepository.findAllByUserId(uuid);
        if (sessions.isEmpty()) {
            throw new AppException("No sessions found with payload");
        }
        return sessions.stream()
                .map(session -> userMapper.sessionToDto(session))
                .collect(Collectors.toList());
    }

    public void updatedSessions(SessionRequest request) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = jwt.getTokenValue();
        Session session = sessionRepository.findByPayload(token)
                .orElseThrow(() -> new AppException("Session not found"));
        session.setAddress(request.getAddress());
        session.setLatitude(request.getLatitude());
        session.setLongitude(request.getLongitude());
        sessionRepository.save(session);
    }

}
