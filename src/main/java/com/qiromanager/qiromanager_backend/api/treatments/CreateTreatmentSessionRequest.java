package com.qiromanager.qiromanager_backend.api.treatments;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateTreatmentSessionRequest {

    @NotNull(message = "Session date is required")
    @PastOrPresent(message = "Session date cannot be in the future")
    private LocalDateTime sessionDate;

    private String notes;
}