package com.qiromanager.qiromanager_backend.domain.exceptions;

public class UnauthorizedRoleException extends RuntimeException {
    public UnauthorizedRoleException() {
        super("You do not have permission to perform this action");
    }
}
