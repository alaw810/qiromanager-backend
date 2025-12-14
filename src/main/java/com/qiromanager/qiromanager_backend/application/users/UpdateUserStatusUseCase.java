package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.api.users.UpdateUserStatusRequest;
import com.qiromanager.qiromanager_backend.api.users.UserResponse;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserNotFoundException;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserStatusUseCase {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse execute(Long id, UpdateUserStatusRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setActive(request.getActive());

        User updated = userRepository.save(user);

        return UserResponse.builder()
                .id(updated.getId())
                .fullName(updated.getFullName())
                .email(updated.getEmail())
                .username(updated.getUsername())
                .role(updated.getRole().name())
                .active(updated.isActive())
                .build();
    }
}
