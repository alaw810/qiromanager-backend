package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.api.mappers.UserMapper;
import com.qiromanager.qiromanager_backend.api.users.UpdateUserStatusRequest;
import com.qiromanager.qiromanager_backend.api.users.UserResponse;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserNotFoundException;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUserStatusUseCase {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse execute(Long id, UpdateUserStatusRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (Boolean.TRUE.equals(request.getActive())) {
            user.activate();
            log.info("User ID: {} status changed to ACTIVE", id);
        } else {
            user.deactivate();
            log.info("User ID: {} status changed to INACTIVE", id);
        }

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }
}
