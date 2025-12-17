package com.qiromanager.qiromanager_backend.api.patients;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePatientStatusRequest {

    @NotNull(message = "Active status is required")
    private Boolean active;
}
