package com.qiromanager.qiromanager_backend.application.auth;

import com.qiromanager.qiromanager_backend.api.auth.AuthResponse;
import com.qiromanager.qiromanager_backend.api.auth.RegisterRequest;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserAlreadyExistsException;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import com.qiromanager.qiromanager_backend.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse execute(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("username");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email");
        }

        try {
            User newUser = User.create(
                    request.getFullName(),
                    request.getUsername(),
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword()),
                    Role.USER
            );

            User saved = userRepository.save(newUser);

            log.info("New user registered: ID {}", saved.getId());

            String token = jwtUtil.generateToken(saved);

            return AuthResponse.builder()
                    .id(saved.getId())
                    .username(saved.getUsername())
                    .role(saved.getRole().name())
                    .token(token)
                    .build();

        } catch (DataIntegrityViolationException e) {
            log.error("Database duplicate detected for: {}", request.getUsername());
            throw new UserAlreadyExistsException("User or email already exists (concurrency)");
        }
    }
}
