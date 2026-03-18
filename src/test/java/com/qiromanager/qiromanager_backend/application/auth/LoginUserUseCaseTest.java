package com.qiromanager.qiromanager_backend.application.auth;

import com.qiromanager.qiromanager_backend.api.auth.AuthResponse;
import com.qiromanager.qiromanager_backend.api.auth.LoginRequest;
import com.qiromanager.qiromanager_backend.domain.exceptions.InvalidCredentialsException;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserInactiveException;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import com.qiromanager.qiromanager_backend.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LoginUserUseCase loginUserUseCase;

    private User activeUser;
    private LoginRequest request;

    @BeforeEach
    void setUp() {
        activeUser = User.create("Jane Doe", "janedoe", "jane@clinic.com", "encodedPass", Role.USER);
        activeUser.forceId(1L);

        request = new LoginRequest();
        request.setUsername("janedoe");
        request.setPassword("rawPassword");
    }

    @Test
    void execute_shouldReturnToken_whenCredentialsAreValid() {
        when(userRepository.findByUsername("janedoe")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("rawPassword", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken(activeUser)).thenReturn("jwt-token");

        AuthResponse response = loginUserUseCase.execute(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("janedoe");
        assertThat(response.getRole()).isEqualTo("USER");
    }

    @Test
    void execute_shouldThrowInvalidCredentials_whenUserNotFound() {
        when(userRepository.findByUsername("janedoe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUserUseCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void execute_shouldThrowInvalidCredentials_whenPasswordIsWrong() {
        when(userRepository.findByUsername("janedoe")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("rawPassword", "encodedPass")).thenReturn(false);

        assertThatThrownBy(() -> loginUserUseCase.execute(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void execute_shouldThrowUserInactive_whenUserIsDisabled() {
        activeUser.deactivate();

        when(userRepository.findByUsername("janedoe")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("rawPassword", "encodedPass")).thenReturn(true);

        assertThatThrownBy(() -> loginUserUseCase.execute(request))
                .isInstanceOf(UserInactiveException.class);

        verify(jwtUtil, never()).generateToken(any());
    }
}
