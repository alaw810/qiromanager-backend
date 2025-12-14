package com.qiromanager.qiromanager_backend.api.mappers;

import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.api.patients.TherapistSummary;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;

import java.util.List;

public class PatientMapper {

    public static PatientResponse toResponse(Patient patient) {
        if (patient == null) return null;

        List<TherapistSummary> therapists =
                patient.getTherapists().stream()
                        .map(TherapistMapper::toSummary)
                        .toList();

        return PatientResponse.builder()
                .id(patient.getId())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .generalNotes(patient.getGeneralNotes())
                .active(patient.isActive())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .therapists(therapists)
                .build();
    }
}
