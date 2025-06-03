package com.snd.server.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.snd.server.exception.AppException;
import com.snd.server.model.Session;
import com.snd.server.model.User;
import com.snd.server.repository.SessionRepository;
import com.snd.server.repository.UserRepository;
import com.snd.server.utils.DateTimeUtils;

import eu.bitwalker.useragentutils.UserAgent;

import java.io.IOException;
import java.util.List;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public CustomLoginSuccessHandler(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return (xfHeader != null) ? xfHeader.split(",")[0] : request.getRemoteAddr();
    }

    private String getDeviceInfo(HttpServletRequest request) {
        String userAgentString = request.getHeader("User-Agent");
        if (userAgentString == null) {
            return "Unknown Device";
        }
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        String browser = userAgent.getBrowser().getName();
        String os = userAgent.getOperatingSystem().getName();
        return browser + " on " + os;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        String ipAddress = getClientIP(request);
        String userAgent = getDeviceInfo(request);

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found: " + email));
        List<Session> existingSessions = sessionRepository.findTop3ByUserOrderByLastActivityDesc(user);
        if (existingSessions.size() >= 3) {
            Session oldestSession = existingSessions.get(existingSessions.size() - 1);
            sessionRepository.delete(oldestSession);
        }

        Object details = authentication.getDetails();
        String token = null;
        if (details instanceof String) {
            token = (String) details;
        } else {
            System.out.println("Authentication details không phải token JWT");
        }

        Session session = Session.builder()
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .payload(token)
                .flag(true)
                .lastActivity(DateTimeUtils.instantNowInVietnam())
                .build();
        sessionRepository.save(session);

    }
}