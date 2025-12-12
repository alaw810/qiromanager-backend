package com.qiromanager.qiromanager_backend.api.patients;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TherapistSummary {
    private Long id;
    private String fullName;
}
