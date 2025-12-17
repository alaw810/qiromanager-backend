package com.qiromanager.qiromanager_backend.api.mappers;

import com.qiromanager.qiromanager_backend.api.users.UserResponse;
import com.qiromanager.qiromanager_backend.domain.user.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        if (user == null) return null;

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
