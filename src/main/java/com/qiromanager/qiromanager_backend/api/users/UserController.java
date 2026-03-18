package com.qiromanager.qiromanager_backend.api.users;

import com.qiromanager.qiromanager_backend.api.mappers.UserMapper;
import com.qiromanager.qiromanager_backend.application.users.*;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "User management. Admin endpoints require ADMIN role.")
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

    @Operation(summary = "List all users, optionally filtered by role (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "List of users")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(required = false) String role
    ) {
        Role parsedRole = null;
        if (role != null && !role.isBlank()) {
            parsedRole = Role.valueOf(role.toUpperCase());
            log.info("Request received: List users filtered by role={} (ADMIN action)", parsedRole);
        } else {
            log.info("Request received: List all users (ADMIN action)");
        }

        List<User> users = listUsersUseCase.execute(parsedRole);

        log.debug("Returning {} users", users.size());
        return ResponseEntity.ok(users.stream()
                .map(UserMapper::toResponse)
                .toList());
    }

    @Operation(summary = "Get user by ID (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Request received: Get user details for ID: {}", id);
        User user = getUserByIdUseCase.execute(id);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    @Operation(summary = "Get the profile of the authenticated user")
    @ApiResponse(responseCode = "200", description = "Authenticated user profile")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserResponse> getMe() {
        log.info("Request received: Get authenticated user profile");
        User myUser = getAuthenticatedUserUseCase.execute();

        return ResponseEntity.ok(UserMapper.toResponse(myUser));
    }

    @Operation(summary = "Update own name and/or password")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserResponse> updateMyProfile(
            @RequestBody @Valid UpdateUserProfileRequest request
    ) {
        log.info("Request received: Update own profile");
        UserResponse response = updateUserProfileUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update any user's data including role (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Username or email already taken")
    })
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

    @Operation(summary = "Activate or deactivate a user account (ADMIN only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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