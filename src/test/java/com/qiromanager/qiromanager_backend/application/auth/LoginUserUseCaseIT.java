package com.qiromanager.qiromanager_backend.application.auth;

import com.qiromanager.qiromanager_backend.api.auth.AuthResponse;
import com.qiromanager.qiromanager_backend.api.auth.LoginRequest;
import com.qiromanager.qiromanager_backend.api.auth.RegisterRequest;
import com.qiromanager.qiromanager_backend.domain.exceptions.InvalidCredentialsException;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserInactiveException;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LoginUserUseCaseIT {

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private LoginUserUseCase loginUserUseCase;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFullName("John Doe");
        registerRequest.setUsername("johndoe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("secret123");

        registerUserUseCase.execute(registerRequest);
    }

    @Test
    void login_successfully_returnsAuthResponse() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("johndoe");
        loginRequest.setPassword("secret123");

        AuthResponse response = loginUserUseCase.execute(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("johndoe");
        assertThat(response.getRole()).isEqualTo("USER");
        assertThat(response.getToken()).isNotBlank();
    }

    @Test
    void login_withIncorrectPassword_throwsInvalidCredentialsException() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("johndoe");
        loginRequest.setPassword("wrongpassword");

        assertThatThrownBy(() -> loginUserUseCase.execute(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void login_withNonExistingUser_throwsInvalidCredentialsException() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("unknown");
        loginRequest.setPassword("secret123");

        assertThatThrownBy(() -> loginUserUseCase.execute(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void login_withDisabledUser_throwsUserInactiveException() {
        User user = userRepository.findByUsername("johndoe").orElseThrow();
        user.setActive(false);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("johndoe");
        loginRequest.setPassword("secret123");

        assertThatThrownBy(() -> loginUserUseCase.execute(loginRequest))
                .isInstanceOf(UserInactiveException.class)
                .hasMessage("User account is inactive");
    }
}