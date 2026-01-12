package com.qiromanager.qiromanager_backend.api.auth;

import com.qiromanager.qiromanager_backend.application.auth.LoginUserUseCase;
import com.qiromanager.qiromanager_backend.application.auth.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Request received: Register new user with username: {}", request.getUsername());

        AuthResponse response = registerUserUseCase.execute(request);

        log.debug("User registered successfully: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Request received: Login attempt for username: {}", request.getUsername());

        AuthResponse response = loginUserUseCase.execute(request);

        log.debug("Login successful for username: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }
}