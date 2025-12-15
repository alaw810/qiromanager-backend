package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.api.mappers.UserMapper;
import com.qiromanager.qiromanager_backend.api.users.UserResponse;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserNotFoundException;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAuthenticatedUserUseCase {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse execute() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return UserMapper.toResponse(user);
    }
}
