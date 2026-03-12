package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.api.patients.TherapistSummary;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchPatientsUseCase {

    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public Page<PatientResponse> execute(String query, Pageable pageable) {

        return patientRepository.searchByFullName(query, pageable)
                .map(patient -> {
                    var therapistSummaries = patient.getTherapists().stream()
                            .map(t -> TherapistSummary.builder()
                                    .id(t.getId())
                                    .fullName(t.getFullName())
                                    .build())
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
                            .therapists(therapistSummaries)
                            .build();
                });
    }
}
