package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.domain.exceptions.UserNotFoundException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    public boolean isAssignedToPatient(User user, Patient patient) {
        if (patient.getTherapists() == null || patient.getTherapists().isEmpty()) {
            return false;
        }

        return patient.getTherapists()
                .stream()
                .anyMatch(t -> t.getId() != null && t.getId().equals(user.getId()));
    }

}
