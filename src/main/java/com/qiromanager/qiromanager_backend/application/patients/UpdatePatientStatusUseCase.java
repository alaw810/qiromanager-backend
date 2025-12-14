package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.patients.UpdatePatientStatusRequest;
import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.api.patients.TherapistSummary;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdatePatientStatusUseCase {

    private final PatientRepository patientRepository;

    @Transactional
    public PatientResponse execute(Long id, UpdatePatientStatusRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        patient.setActive(request.getActive());

        Patient updated = patientRepository.save(patient);

        List<TherapistSummary> therapistSummaries =
                updated.getTherapists().stream()
                        .map(t -> TherapistSummary.builder()
                                .id(t.getId())
                                .fullName(t.getFullName())
                                .build())
                        .toList();

        return PatientResponse.builder()
                .id(updated.getId())
                .fullName(updated.getFullName())
                .dateOfBirth(updated.getDateOfBirth())
                .phone(updated.getPhone())
                .email(updated.getEmail())
                .address(updated.getAddress())
                .generalNotes(updated.getGeneralNotes())
                .active(updated.isActive())
                .createdAt(updated.getCreatedAt())
                .updatedAt(updated.getUpdatedAt())
                .therapists(therapistSummaries)
                .build();
    }
}
