package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListUsersUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListUsersUseCase listUsersUseCase;

    @Test
    void execute_shouldReturnAllUsers_whenRoleIsNull() {
        User admin = User.create("Admin User", "admin", "admin@clinic.com", "pass", Role.ADMIN);
        User therapist = User.create("Therapist", "therapist", "therapist@clinic.com", "pass", Role.USER);

        when(userRepository.findAll()).thenReturn(List.of(admin, therapist));

        List<User> result = listUsersUseCase.execute(null);

        assertThat(result).hasSize(2);
        verify(userRepository).findAll();
        verify(userRepository, never()).findByRole(any());
    }

    @Test
    void execute_shouldReturnFilteredUsers_whenRoleIsUser() {
        User therapist = User.create("Therapist", "therapist", "therapist@clinic.com", "pass", Role.USER);

        when(userRepository.findByRole(Role.USER)).thenReturn(List.of(therapist));

        List<User> result = listUsersUseCase.execute(Role.USER);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(Role.USER);
        verify(userRepository).findByRole(Role.USER);
        verify(userRepository, never()).findAll();
    }

    @Test
    void execute_shouldReturnFilteredUsers_whenRoleIsAdmin() {
        User admin = User.create("Admin User", "admin", "admin@clinic.com", "pass", Role.ADMIN);

        when(userRepository.findByRole(Role.ADMIN)).thenReturn(List.of(admin));

        List<User> result = listUsersUseCase.execute(Role.ADMIN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(Role.ADMIN);
        verify(userRepository).findByRole(Role.ADMIN);
        verify(userRepository, never()).findAll();
    }
}
