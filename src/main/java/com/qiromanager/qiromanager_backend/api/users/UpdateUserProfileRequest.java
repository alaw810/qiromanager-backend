package com.qiromanager.qiromanager_backend.api.users;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    private String fullName;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}