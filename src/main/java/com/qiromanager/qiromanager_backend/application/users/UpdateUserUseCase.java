package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.api.mappers.UserMapper;
import com.qiromanager.qiromanager_backend.api.users.UpdateUserRequest;
import com.qiromanager.qiromanager_backend.api.users.UserResponse;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserAlreadyExistsException;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserNotFoundException;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUserUseCase {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse execute(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.findByUsername(request.getUsername())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    log.warn("Update failed: Username '{}' already taken", request.getUsername());
                    throw new UserAlreadyExistsException("Username already exists");
                });

        userRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    log.warn("Update failed: Email '{}' already taken", request.getEmail());
                    throw new UserAlreadyExistsException("Email already exists");
                });

        Role newRole = request.getRole() != null ? Role.valueOf(request.getRole()) : null;

        log.info("Admin updating User ID: {} (New Role: {}, Username: {})", id, newRole, request.getUsername());

        user.updateProfile(request.getFullName(), request.getUsername(), request.getEmail(), newRole);

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }
}