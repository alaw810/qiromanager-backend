package com.qiromanager.qiromanager_backend.api.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiromanager.qiromanager_backend.application.users.*;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ListUsersUseCase listUsersUseCase;
    @MockitoBean private GetUserByIdUseCase getUserByIdUseCase;
    @MockitoBean private UpdateUserUseCase updateUserUseCase;
    @MockitoBean private UpdateUserStatusUseCase updateUserStatusUseCase;
    @MockitoBean private GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;
    @MockitoBean private UpdateUserProfileUseCase updateUserProfileUseCase;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllUsers_ShouldReturn200_WhenUserIsAdmin() throws Exception {
        User user = User.create("Test User", "test", "test@test.com", "pass", Role.USER);
        user.forceId(1L);
        when(listUsersUseCase.execute()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("test"));
    }

    @Test
    @WithMockUser(username = "simpleuser", roles = {"USER"})
    void getAllUsers_ShouldReturn403_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "me", roles = {"USER"})
    void updateMyProfile_ShouldReturn200() throws Exception {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setFullName("New Name");

        UserResponse response = UserResponse.builder().username("me").fullName("New Name").build();
        when(updateUserProfileUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("New Name"));
    }

    @Test
    @WithMockUser(username = "myuser", roles = {"USER"})
    void getMe_ShouldReturnMyProfile() throws Exception {
        User user = User.create("My User", "myuser", "my@test.com", "pass", Role.USER);
        user.forceId(10L);

        when(getAuthenticatedUserUseCase.execute()).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("myuser"))
                .andExpect(jsonPath("$.email").value("my@test.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUserById_ShouldReturnUser_WhenAdmin() throws Exception {
        User user = User.create("Target User", "target", "target@test.com", "pass", Role.USER);
        user.forceId(5L);

        when(getUserByIdUseCase.execute(5L)).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.username").value("target"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUser_ShouldReturnUpdatedUser_WhenRequestIsValid() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("Updated Name");
        request.setUsername("updated_user");
        request.setEmail("updated@test.com");
        request.setRole("ADMIN");

        UserResponse response = UserResponse.builder()
                .id(2L)
                .username("updated_user")
                .role("ADMIN")
                .build();

        when(updateUserUseCase.execute(eq(2L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUser_ShouldReturn400_WhenRoleIsInvalid() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFullName("Name");
        request.setUsername("user");
        request.setEmail("email@test.com");
        request.setRole("SUPER_GOD_MODE");

        mockMvc.perform(put("/api/v1/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserStatus_ShouldActivateUser() throws Exception {
        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setActive(true);

        UserResponse response = UserResponse.builder().id(3L).active(true).build();

        when(updateUserStatusUseCase.execute(eq(3L), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/users/3/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(username = "regular", roles = {"USER"})
    void updateUserStatus_ShouldReturn403_WhenNotAdmin() throws Exception {
        UpdateUserStatusRequest request = new UpdateUserStatusRequest();
        request.setActive(false);

        mockMvc.perform(patch("/api/v1/users/3/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "me", roles = {"USER"})
    void updateMyProfile_ShouldReturn400_WhenPasswordTooShort() throws Exception {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest();
        request.setPassword("123");

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}