package com.qiromanager.qiromanager_backend.application.users;

import com.qiromanager.qiromanager_backend.api.users.UpdateUserProfileRequest;
import com.qiromanager.qiromanager_backend.api.users.UserResponse;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfileUseCaseTest {

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UpdateUserProfileUseCase updateUserProfileUseCase;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = User.create("Jane Doe", "janedoe", "jane@clinic.com", "encodedOldPass", Role.USER);
        currentUser.forceId(1L);
        when(authenticatedUserService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void execute_shouldUpdateFullName_whenProvided() {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setFullName("Jane Updated");

        UserResponse response = updateUserProfileUseCase.execute(request);

        assertThat(response.getFullName()).isEqualTo("Jane Updated");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository).save(currentUser);
    }

    @Test
    void execute_shouldChangePassword_whenProvided() {
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPass");

        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setPassword("newPassword123");

        updateUserProfileUseCase.execute(request);

        verify(passwordEncoder).encode("newPassword123");
        assertThat(currentUser.getPassword()).isEqualTo("encodedNewPass");
        verify(userRepository).save(currentUser);
    }

    @Test
    void execute_shouldUpdateBoth_whenBothFieldsProvided() {
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPass");

        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setFullName("Jane Updated");
        request.setPassword("newPassword123");

        UserResponse response = updateUserProfileUseCase.execute(request);

        assertThat(response.getFullName()).isEqualTo("Jane Updated");
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(currentUser);
    }

    @Test
    void execute_shouldSave_withoutChanges_whenBothFieldsAreBlank() {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setFullName("  ");
        request.setPassword("  ");

        updateUserProfileUseCase.execute(request);

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository).save(currentUser);
    }
}
