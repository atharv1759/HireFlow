package com.hireflow.service.impl;

import com.hireflow.dto.request.AuthRequest;
import com.hireflow.dto.response.ApiResponse;
import com.hireflow.entity.User;
import com.hireflow.exception.DuplicateResourceException;
import com.hireflow.repository.UserRepository;
import com.hireflow.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public ApiResponse.AuthResponse register(AuthRequest.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .build();

        // Set default company name for company accounts
        if (request.getRole() == User.Role.COMPANY) {
            user.setCompanyName(request.getName());
        }

        user = userRepository.save(user);
        log.info("New user registered: {} ({})", user.getEmail(), user.getRole());

        String token = jwtUtils.generateTokenFromEmail(user.getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return buildAuthResponse(user, token, refreshToken);
    }

    public ApiResponse.AuthResponse login(AuthRequest.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User logged in: {}", user.getEmail());
        return buildAuthResponse(user, token, refreshToken);
    }

    public ApiResponse.AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
        String email = jwtUtils.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newToken = jwtUtils.generateTokenFromEmail(email);
        String newRefreshToken = jwtUtils.generateRefreshToken(email);

        return buildAuthResponse(user, newToken, newRefreshToken);
    }

    private ApiResponse.AuthResponse buildAuthResponse(User user, String token, String refreshToken) {
        return ApiResponse.AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresIn(jwtUtils.getExpirationMs())
                .build();
    }
}
