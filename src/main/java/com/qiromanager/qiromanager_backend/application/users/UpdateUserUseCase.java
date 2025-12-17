package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.api.users.UpdateUserRequest;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserAlreadyExistsException;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserNotFoundException;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserRepository userRepository;

    @Transactional
    public User execute(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.findByUsername(request.getUsername())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new UserAlreadyExistsException("Username already exists");
                });

        userRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new UserAlreadyExistsException("Email already exists");
                });

        Role newRole = request.getRole() != null ? Role.valueOf(request.getRole()) : null;

        user.updateProfile(request.getFullName(), request.getUsername(), request.getEmail(), newRole);

        return userRepository.save(user);
    }
}