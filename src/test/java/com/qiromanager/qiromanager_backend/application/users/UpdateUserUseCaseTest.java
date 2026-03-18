package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.api.users.UpdateUserRequest;
import com.qiromanager.qiromanager_backend.api.users.UserResponse;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserAlreadyExistsException;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserNotFoundException;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
class UpdateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = User.create("Jane Doe", "janedoe", "jane@clinic.com", "pass", Role.USER);
        existingUser.forceId(1L);
    }

    @Test
    void execute_shouldUpdateUser_whenDataIsValid() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("Jane Updated");
        request.setUsername("janedoe");
        request.setEmail("jane@clinic.com");
        request.setRole("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("janedoe")).thenReturn(Optional.of(existingUser)); // same user
        when(userRepository.findByEmail("jane@clinic.com")).thenReturn(Optional.of(existingUser)); // same user
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = updateUserUseCase.execute(1L, request);

        assertThat(response.getFullName()).isEqualTo("Jane Updated");
        assertThat(response.getRole()).isEqualTo("ADMIN");
        verify(userRepository).save(existingUser);
    }

    @Test
    void execute_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("Name");
        request.setUsername("user");
        request.setEmail("user@test.com");

        assertThatThrownBy(() -> updateUserUseCase.execute(99L, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_shouldThrow_whenUsernameAlreadyTakenByAnotherUser() {
        User otherUser = User.create("Other", "takenusername", "other@clinic.com", "pass", Role.USER);
        otherUser.forceId(2L);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("Jane");
        request.setUsername("takenusername");
        request.setEmail("jane@clinic.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("takenusername")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> updateUserUseCase.execute(1L, request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Username");

        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_shouldThrow_whenEmailAlreadyTakenByAnotherUser() {
        User otherUser = User.create("Other", "other", "taken@clinic.com", "pass", Role.USER);
        otherUser.forceId(2L);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("Jane");
        request.setUsername("janedoe");
        request.setEmail("taken@clinic.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("janedoe")).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("taken@clinic.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> updateUserUseCase.execute(1L, request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Email");

        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_shouldAllowUpdate_whenUsernameAndEmailBelongToSameUser() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("Jane Doe");
        request.setUsername("janedoe");
        request.setEmail("jane@clinic.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("janedoe")).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("jane@clinic.com")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = updateUserUseCase.execute(1L, request);

        assertThat(response).isNotNull();
        verify(userRepository).save(existingUser);
    }
}
