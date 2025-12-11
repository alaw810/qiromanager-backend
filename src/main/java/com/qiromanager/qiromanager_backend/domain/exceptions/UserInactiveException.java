package com.qiromanager.qiromanager_backend.domain.exceptions;

public class UserInactiveException extends RuntimeException {
    public UserInactiveException() {
        super("User account is inactive");
    }
}
