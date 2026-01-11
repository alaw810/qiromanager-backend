package com.qiromanager.qiromanager_backend.api.users;

import com.qiromanager.qiromanager_backend.api.mappers.UserMapper;
import com.qiromanager.qiromanager_backend.application.users.*;
import com.qiromanager.qiromanager_backend.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final ListUsersUseCase listUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UpdateUserStatusUseCase updateUserStatusUseCase;
    private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Request received: List all users (ADMIN action)");

        List<User> users = listUsersUseCase.execute();

        log.debug("Returning {} users", users.size());
        return ResponseEntity.ok(users.stream()
                .map(UserMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Request received: Get user details for ID: {}", id);
        User user = getUserByIdUseCase.execute(id);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserResponse> getMe() {
        log.info("Request received: Get authenticated user profile");
        User myUser = getAuthenticatedUserUseCase.execute();

        return ResponseEntity.ok(UserMapper.toResponse(myUser));
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserResponse> updateMyProfile(
            @RequestBody @Valid UpdateUserProfileRequest request
    ) {
        log.info("Request received: Update own profile");
        UserResponse response = updateUserProfileUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest request
    ) {
        log.info("Request received: Admin update for User ID: {}", id);
        UserResponse response = updateUserUseCase.execute(id, request);

        log.debug("User ID: {} updated successfully", id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserStatusRequest request
    ) {
        log.info("Request received: Change status for User ID: {} to active={}", id, request.getActive());

        UserResponse response = updateUserStatusUseCase.execute(id, request);

        log.debug("User ID: {} status changed successfully", id);
        return ResponseEntity.ok(response);
    }

}