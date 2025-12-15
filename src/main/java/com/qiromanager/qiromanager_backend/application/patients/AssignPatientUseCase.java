package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.mappers.PatientMapper;
import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.api.patients.TherapistSummary;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignPatientUseCase {

    private final PatientRepository patientRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public PatientResponse execute(Long patientId) {

        User currentUser = authenticatedUserService.getCurrentUser();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        boolean alreadyAssigned = patient.getTherapists().stream()
                .anyMatch(t -> t.getId() != null && t.getId().equals(currentUser.getId()));

        if (!alreadyAssigned) {
            patient.getTherapists().add(currentUser);
        }

        Patient updated = patientRepository.save(patient);

        List<TherapistSummary> therapistSummaries =
                updated.getTherapists().stream()
                        .map(t -> TherapistSummary.builder()
                                .id(t.getId())
                                .fullName(t.getFullName())
                                .build())
                        .toList();

        return PatientMapper.toResponse(updated);
    }
}
