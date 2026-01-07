package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.api.users.UpdateUserProfileRequest;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserProfileUseCase {

    private final AuthenticatedUserService authenticatedUserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User execute(UpdateUserProfileRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            currentUser.updateProfile(
                    request.getFullName(),
                    currentUser.getUsername(),
                    currentUser.getEmail(),
                    currentUser.getRole()
            );
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            currentUser.changePassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(currentUser);
    }
}