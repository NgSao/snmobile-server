package com.snd.server.security;

import com.snd.server.constant.SecurityConstant;
import com.snd.server.dto.response.DataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public CustomAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        DataResponse<Object> res = new DataResponse<>();
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.setMessage(SecurityConstant.ACCESS_DENIED);
        res.setError(accessDeniedException.getMessage());
        response.getWriter().write(mapper.writeValueAsString(res));
    }
}
