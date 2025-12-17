package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.mappers.PatientMapper;
import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.api.patients.UpdatePatientRequest;
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
public class UpdatePatientUseCase {

    private final PatientRepository patientRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public PatientResponse execute(Long id, UpdatePatientRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        User currentUser = authenticatedUserService.getCurrentUser();
        authenticatedUserService.assertCanAccessPatient(currentUser, patient);

        patient.update(
                request.getFullName(),
                request.getDateOfBirth(),
                request.getPhone(),
                request.getEmail(),
                request.getAddress(),
                request.getGeneralNotes()
        );

        Patient updated = patientRepository.save(patient);

        return PatientMapper.toResponse(updated);
    }
}
