package com.qiromanager.qiromanager_backend.api.patients;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PatientResponse {

    private Long id;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phone;
    private String email;
    private String address;
    private String generalNotes;
    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<TherapistSummary> therapists;
}
