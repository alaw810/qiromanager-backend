package com.qiromanager.qiromanager_backend.api.users;

import com.qiromanager.qiromanager_backend.application.users.GetUserByIdUseCase;
import com.qiromanager.qiromanager_backend.application.users.ListUsersUseCase;
import com.qiromanager.qiromanager_backend.application.users.UpdateUserStatusUseCase;
import com.qiromanager.qiromanager_backend.application.users.UpdateUserUseCase;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = listUsersUseCase.execute();

        List<UserResponse> response = users.stream()
                .map(this::toUserResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = getUserByIdUseCase.execute(id);

        return ResponseEntity.ok(toUserResponse(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest request
    ) {
        return ResponseEntity.ok(updateUserUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserStatusRequest request
    ) {
        return ResponseEntity.ok(updateUserStatusUseCase.execute(id, request));
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().name())
                .active(user.isActive())
                .build();
    }
}