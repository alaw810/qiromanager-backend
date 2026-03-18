package com.qiromanager.qiromanager_backend.api.auth;

import com.qiromanager.qiromanager_backend.application.auth.LoginUserUseCase;
import com.qiromanager.qiromanager_backend.application.auth.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "Authentication", description = "Register and login endpoints. No token required.")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    @Operation(summary = "Register a new user (ADMIN only in production)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Request received: Register new user with username: {}", request.getUsername());

        AuthResponse response = registerUserUseCase.execute(request);

        log.debug("User registered successfully: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login and obtain a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, returns JWT token"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password"),
            @ApiResponse(responseCode = "403", description = "Account is inactive")
    })
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Request received: Login attempt for username: {}", request.getUsername());

        AuthResponse response = loginUserUseCase.execute(request);

        log.debug("Login successful for username: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }
}