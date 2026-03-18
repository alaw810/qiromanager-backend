package com.qiromanager.qiromanager_backend.domain.exceptions;

public class ClinicalRecordNotFoundException extends RuntimeException {
    public ClinicalRecordNotFoundException(Long id) {
        super("Clinical record with id " + id + " not found");
    }
}
