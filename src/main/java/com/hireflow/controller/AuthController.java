package com.hireflow.controller;

import com.hireflow.dto.request.AuthRequest;
import com.hireflow.dto.response.ApiResponse;
import com.hireflow.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse.AuthResponse> register(
            @Valid @RequestBody AuthRequest.RegisterRequest request) {
        ApiResponse.AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse.AuthResponse> login(
            @Valid @RequestBody AuthRequest.LoginRequest request) {
        ApiResponse.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse.AuthResponse> refreshToken(
            @Valid @RequestBody AuthRequest.RefreshTokenRequest request) {
        ApiResponse.AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse.MessageResponse> checkAuth() {
        return ResponseEntity.ok(
                ApiResponse.MessageResponse.builder()
                        .success(true)
                        .message("Token is valid")
                        .build()
        );
    }
}
