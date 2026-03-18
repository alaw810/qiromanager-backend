package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.api.users.UpdateUserStatusRequest;
import com.qiromanager.qiromanager_backend.api.users.UserResponse;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserNotFoundException;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserStatusUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateUserStatusUseCase updateUserStatusUseCase;

    @Test
    void execute_shouldActivateUser_whenRequestIsTrue() {
        User user = User.create("Jane Doe", "janedoe", "jane@clinic.com", "pass", Role.USER);
        user.deactivate();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setActive(true);

        UserResponse response = updateUserStatusUseCase.execute(1L, request);

        assertThat(response.isActive()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void execute_shouldDeactivateUser_whenRequestIsFalse() {
        User user = User.create("Jane Doe", "janedoe", "jane@clinic.com", "pass", Role.USER);

        assertThat(user.isActive()).isTrue();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setActive(false);

        UserResponse response = updateUserStatusUseCase.execute(1L, request);

        assertThat(response.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    void execute_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setActive(true);

        assertThatThrownBy(() -> updateUserStatusUseCase.execute(99L, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
    }
}
