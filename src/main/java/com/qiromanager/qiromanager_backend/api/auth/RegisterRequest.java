package com.qiromanager.qiromanager_backend.api.auth;

import lombok.Data;

@Data
public class RegisterRequest {

    private String fullName;
    private String email;
    private String username;
    private String password;

}
