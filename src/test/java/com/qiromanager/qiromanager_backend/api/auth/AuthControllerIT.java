package com.qiromanager.qiromanager_backend.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiromanager.qiromanager_backend.application.auth.LoginUserUseCase;
import com.qiromanager.qiromanager_backend.application.auth.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUserUseCase loginUserUseCase;

    @Test
    void register_ShouldReturn200_WhenRequestIsValid() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("New User");
        request.setUsername("newuser");
        request.setEmail("new@test.com");
        request.setPassword("password123");

        AuthResponse response = AuthResponse.builder()
                .id(1L)
                .username("newuser")
                .role("USER")
                .token("mock-jwt-token")
                .build();

        when(registerUserUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void register_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("User");
        request.setUsername("user");
        request.setEmail("invalid-email");
        request.setPassword("pass");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturn200_WhenCredentialsAreCorrect() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("existing");
        request.setPassword("password");

        AuthResponse response = AuthResponse.builder()
                .token("login-token")
                .build();

        when(loginUserUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("login-token"));
    }

    @Test
    void login_ShouldReturn400_WhenFieldsAreEmpty() throws Exception {
        LoginRequest request = new LoginRequest();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}