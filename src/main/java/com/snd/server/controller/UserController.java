package com.snd.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snd.server.constant.ApiPathConstant;
import com.snd.server.dto.request.CustomerUserRequest;
import com.snd.server.dto.request.ResetPasswordRequest;
import com.snd.server.dto.response.CustomerUserResponse;
import com.snd.server.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/customer")
    public ResponseEntity<CustomerUserResponse> getUserByToken() {
        return ResponseEntity.ok().body(userService.getUserByToken());
    }

    @PutMapping("/customer/updated")
    public ResponseEntity<CustomerUserResponse> updateAccount(@RequestBody CustomerUserRequest request) {
        return ResponseEntity.ok().body(userService.updateAccount(request));
    }

    @PostMapping("/customer/reset-password")
    public ResponseEntity<Boolean> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok().body(true);
    }

}
