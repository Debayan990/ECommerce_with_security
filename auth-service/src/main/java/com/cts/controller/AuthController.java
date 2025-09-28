package com.cts.controller;

import com.cts.dtos.AuthResponse;
import com.cts.dtos.LoginDto;
import com.cts.dtos.RegisterDto;
import com.cts.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = {"/login", "/signin"})
    public AuthResponse login(@RequestBody LoginDto loginDto){
        String token =  authService.login(loginDto);
        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        return response;
    }

    @PostMapping(path = {"/register", "/signup"})
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        return ResponseEntity.ok(authService.register(registerDto));
    }
}