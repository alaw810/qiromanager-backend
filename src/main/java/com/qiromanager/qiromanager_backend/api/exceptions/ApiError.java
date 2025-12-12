package com.qiromanager.qiromanager_backend.api.exceptions;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ApiError {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiError(int status, String error, String message, String path) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
