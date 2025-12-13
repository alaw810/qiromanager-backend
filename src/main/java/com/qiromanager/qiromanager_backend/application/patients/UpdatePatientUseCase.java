package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.api.patients.TherapistSummary;
import com.qiromanager.qiromanager_backend.api.patients.UpdatePatientRequest;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdatePatientUseCase {

    private final PatientRepository patientRepository;

    @Transactional
    public PatientResponse execute(Long id, UpdatePatientRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        patient.setFullName(request.getFullName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());
        patient.setGeneralNotes(request.getGeneralNotes());

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
