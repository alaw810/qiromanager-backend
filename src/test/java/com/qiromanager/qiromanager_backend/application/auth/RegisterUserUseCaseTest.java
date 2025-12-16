package com.qiromanager.qiromanager_backend.application.auth;

import com.qiromanager.qiromanager_backend.api.auth.AuthResponse;
import com.qiromanager.qiromanager_backend.api.auth.RegisterRequest;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserAlreadyExistsException;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import com.qiromanager.qiromanager_backend.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void execute_shouldRegisterUser_whenDataIsValid() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Unit Test");
        request.setUsername("unittest");
        request.setEmail("unit@test.com");
        request.setPassword("password");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        when(jwtUtil.generateToken(any(User.class))).thenReturn("mocked-token");

        AuthResponse response = registerUserUseCase.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("unittest");
        assertThat(response.getToken()).isEqualTo("mocked-token");
        assertThat(response.getRole()).isEqualTo("USER");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void execute_shouldThrowException_whenUsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");
        request.setEmail("new@email.com");

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThatThrownBy(() -> registerUserUseCase.execute(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("username");

        verify(userRepository, never()).save(any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void execute_shouldThrowException_whenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setEmail("existing@email.com");

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        assertThatThrownBy(() -> registerUserUseCase.execute(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("email");

        verify(userRepository, never()).save(any());
    }
}