package com.qiromanager.qiromanager_backend.application.auth;

import com.qiromanager.qiromanager_backend.api.auth.AuthResponse;
import com.qiromanager.qiromanager_backend.api.auth.LoginRequest;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import com.qiromanager.qiromanager_backend.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public AuthResponse execute(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        if (user == null) {
            log.warn("Authentication failed for username: {}", request.getUsername());
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Authentication failed for username: {}", request.getUsername());
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!user.isActive()) {
            log.warn("Login attempt on disabled account: {}", request.getUsername());
            throw new DisabledException("User account is disabled");
        }

        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .token(token)
                .build();
    }
}

