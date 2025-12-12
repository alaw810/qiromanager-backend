package com.qiromanager.qiromanager_backend.application.auth;

import com.qiromanager.qiromanager_backend.api.auth.AuthResponse;
import com.qiromanager.qiromanager_backend.api.auth.RegisterRequest;
import com.qiromanager.qiromanager_backend.domain.exceptions.UserAlreadyExistsException;
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
class RegisterUserUseCaseIT {

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private UserRepository userRepository;

    private RegisterRequest request;

    @BeforeEach
    void setup() {
        request = new RegisterRequest();
        request.setFullName("John Doe");
        request.setEmail("john@example.com");
        request.setUsername("johndoe");
        request.setPassword("123456");
    }

    @Test
    void registerUser_successfully() {
        AuthResponse result = registerUserUseCase.execute(request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo("johndoe");
        assertThat(result.getRole()).isEqualTo("USER");
        assertThat(result.getToken()).isNotBlank();

        User persisted = userRepository.findById(result.getId()).orElseThrow();
        assertThat(persisted.getUsername()).isEqualTo("johndoe");
        assertThat(persisted.getEmail()).isEqualTo("john@example.com");

        assertThat(persisted.getPassword()).isNotEqualTo("123456");
    }

    @Test
    void registeringUser_withDuplicateEmail_throwsException() {
        registerUserUseCase.execute(request);

        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setFullName("Other");
        duplicate.setUsername("otherusername");
        duplicate.setEmail("john@example.com");
        duplicate.setPassword("123456");

        assertThatThrownBy(() -> registerUserUseCase.execute(duplicate))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email already registered");
    }

    @Test
    void registeringUser_withDuplicateUsername_throwsException() {
        registerUserUseCase.execute(request);

        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setFullName("Other");
        duplicate.setUsername("johndoe");
        duplicate.setEmail("other@example.com");
        duplicate.setPassword("123456");

        assertThatThrownBy(() -> registerUserUseCase.execute(duplicate))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username already exists");
    }
}
