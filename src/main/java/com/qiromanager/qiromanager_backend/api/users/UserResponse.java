package com.qiromanager.qiromanager_backend.api.users;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String username;
    private String role;
    private boolean active;
}
