package com.qiromanager.qiromanager_backend.api.users;

import com.qiromanager.qiromanager_backend.api.mappers.UserMapper;
import com.qiromanager.qiromanager_backend.application.users.*;
import com.qiromanager.qiromanager_backend.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final ListUsersUseCase listUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UpdateUserStatusUseCase updateUserStatusUseCase;
    private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = listUsersUseCase.execute();

        return ResponseEntity.ok(users.stream()
                .map(UserMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = getUserByIdUseCase.execute(id);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        User myUser = getAuthenticatedUserUseCase.execute();

        return ResponseEntity.ok(UserMapper.toResponse(myUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest request
    ) {
        User updatedUser = updateUserUseCase.execute(id, request);
        return ResponseEntity.ok(UserMapper.toResponse(updatedUser));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserStatusRequest request
    ) {
        User updatedUser = updateUserStatusUseCase.execute(id, request);
        return ResponseEntity.ok(UserMapper.toResponse(updatedUser));
    }

}