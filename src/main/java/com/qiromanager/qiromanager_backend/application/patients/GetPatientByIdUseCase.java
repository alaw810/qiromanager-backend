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
public class GetPatientByIdUseCase {

    private final PatientRepository patientRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional(readOnly = true)
    public PatientResponse execute(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        User currentUser = authenticatedUserService.getCurrentUser();
        authenticatedUserService.assertCanAccessPatient(currentUser, patient);

        List<TherapistSummary> therapistSummaries =
                patient.getTherapists().stream()
                        .map(t -> TherapistSummary.builder()
                                .id(t.getId())
                                .fullName(t.getFullName())
                                .build())
                        .toList();

        return PatientMapper.toResponse(patient);
    }
}
