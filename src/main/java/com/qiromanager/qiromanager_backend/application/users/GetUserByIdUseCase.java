package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.domain.exceptions.UserNotFoundException;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase {

    private final UserRepository userRepository;

    public User execute(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}