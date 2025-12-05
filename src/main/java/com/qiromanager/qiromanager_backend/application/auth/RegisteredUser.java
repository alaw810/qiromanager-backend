package com.qiromanager.qiromanager_backend.application.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisteredUser {
    private Long id;
    private String username;
    private String role;
}
