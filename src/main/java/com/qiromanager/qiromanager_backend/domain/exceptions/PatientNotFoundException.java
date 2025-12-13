package com.qiromanager.qiromanager_backend.domain.exceptions;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long id) {
        super("Patient with id " + id + " not found");
    }
}
