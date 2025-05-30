package com.snd.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snd.server.constant.ApiPathConstant;
import com.snd.server.constant.MonoConstant;
import com.snd.server.dto.request.AuthRegisterRequest;
import com.snd.server.dto.request.EmailRequest;
import com.snd.server.dto.request.UserLoginRequest;
import com.snd.server.dto.request.VerifyRequest;
import com.snd.server.dto.response.AuthLoginResponse;
import com.snd.server.service.AuthService;
import com.snd.server.service.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class AuthController {
    private final AuthService authService;
    private final TokenBlacklistService blacklistService;
    private final BearerTokenResolver tokenResolver;

    public AuthController(AuthService authService, TokenBlacklistService blacklistService,
            BearerTokenResolver tokenResolver) {
        this.authService = authService;
        this.blacklistService = blacklistService;
        this.tokenResolver = tokenResolver;
    }

    @PostMapping(ApiPathConstant.AUTH_REGISTER)
    public ResponseEntity<String> registerUser(@Valid @RequestBody AuthRegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok().body(MonoConstant.REGISTER_SUCCESS);
    }

    @PostMapping(ApiPathConstant.AUTH_LOGIN)
    public ResponseEntity<AuthLoginResponse> loginAuth(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok().body(authService.loginUser(request));
    }

    @PostMapping(ApiPathConstant.LOGOUT)
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = tokenResolver.resolve(request);
        if (token != null) {
            blacklistService.logout(token);
        }
        return ResponseEntity.ok("Đăng xuất thành công");
    }

    @PostMapping(ApiPathConstant.AUTH_VERIFY)
    public ResponseEntity<String> verifyUser(@Valid @RequestBody VerifyRequest request) {
        authService.verifyUser(request);
        return ResponseEntity.ok().body(MonoConstant.VERIFY_SUCCESS);
    }

    @PostMapping(ApiPathConstant.AUTH_SEND_OTP)
    public ResponseEntity<String> sendOtp(@RequestBody EmailRequest request) {
        authService.sendOtp(request);
        return ResponseEntity.ok().body(MonoConstant.VERIFY_CODE_SENT);
    }

    @PostMapping(ApiPathConstant.AUTH_FORGOT_PASSWORD)
    public ResponseEntity<String> verifyPassword(@Valid @RequestBody VerifyRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().body(MonoConstant.VERIFY_PASSWORD_RESET_SUCCESS);
    }

}
