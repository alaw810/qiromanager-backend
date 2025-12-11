package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.api.users.UserResponse;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListUsersUseCase {

    private final UserRepository userRepository;

    public List<UserResponse> execute() {
        return userRepository.findAll()
                .stream()
                .map(user ->
                        UserResponse.builder()
                                .id(user.getId())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .username(user.getUsername())
                                .role(user.getRole().name())
                                .active(user.isActive())
                                .build()
                )
                .toList();
    }
}
