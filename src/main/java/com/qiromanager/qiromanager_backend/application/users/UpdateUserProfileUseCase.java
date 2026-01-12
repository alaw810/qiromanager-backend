package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.api.mappers.UserMapper;
import com.qiromanager.qiromanager_backend.api.users.UpdateUserProfileRequest;
import com.qiromanager.qiromanager_backend.api.users.UserResponse;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUserProfileUseCase {

    private final AuthenticatedUserService authenticatedUserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse execute(UpdateUserProfileRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            log.info("User '{}' updating profile details", currentUser.getUsername());
            currentUser.updateProfile(
                    request.getFullName(),
                    currentUser.getUsername(),
                    currentUser.getEmail(),
                    currentUser.getRole()
            );
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            log.info("User '{}' changing password", currentUser.getUsername());
            currentUser.changePassword(passwordEncoder.encode(request.getPassword()));
        }

        User savedUser = userRepository.save(currentUser);
        return UserMapper.toResponse(savedUser);
    }
}