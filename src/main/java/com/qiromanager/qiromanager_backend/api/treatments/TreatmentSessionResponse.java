package com.qiromanager.qiromanager_backend.api.treatments;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TreatmentSessionResponse {
    private Long id;
    private Long patientId;
    private String therapistName;
    private LocalDateTime sessionDate;
    private String notes;
    private LocalDateTime createdAt;
}