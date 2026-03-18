package com.qiromanager.qiromanager_backend.domain.exceptions;

public class TreatmentSessionNotFoundException extends RuntimeException {

    public TreatmentSessionNotFoundException(Long id) {
        super("Treatment session with id " + id + " not found");
    }
}
