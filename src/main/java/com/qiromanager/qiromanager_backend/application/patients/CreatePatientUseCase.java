package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.patients.CreatePatientRequest;
import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.api.patients.TherapistSummary;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePatientUseCase {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public PatientResponse execute(CreatePatientRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User therapist = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Patient patient = Patient.builder()
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .generalNotes(request.getGeneralNotes())
                .active(true)
                .build();

        patient.getTherapists().add(therapist);

        Patient saved = patientRepository.save(patient);

        List<TherapistSummary> therapistSummaries =
                saved.getTherapists().stream()
                        .map(t -> TherapistSummary.builder()
                                .id(t.getId())
                                .fullName(t.getFullName())
                                .build())
                        .toList();

        return PatientResponse.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .dateOfBirth(saved.getDateOfBirth())
                .phone(saved.getPhone())
                .email(saved.getEmail())
                .address(saved.getAddress())
                .generalNotes(saved.getGeneralNotes())
                .active(saved.isActive())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .therapists(therapistSummaries)
                .build();
    }
}
