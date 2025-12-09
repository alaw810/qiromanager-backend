package com.qiromanager.qiromanager_backend.api.auth;

import com.qiromanager.qiromanager_backend.application.auth.RegisterUserUseCase;
import com.qiromanager.qiromanager_backend.application.auth.RegisteredUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        RegisteredUser registered = registerUserUseCase.execute(request);

        AuthResponse response = AuthResponse.builder()
                .id(registered.getId())
                .username(registered.getUsername())
                .role(registered.getRole())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
