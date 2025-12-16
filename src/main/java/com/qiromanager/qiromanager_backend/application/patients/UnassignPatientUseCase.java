package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.mappers.PatientMapper;
import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import com.qiromanager.qiromanager_backend.domain.exceptions.UnauthorizedRoleException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnassignPatientUseCase {

    private final PatientRepository patientRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public PatientResponse execute(Long patientId) {

        User currentUser = authenticatedUserService.getCurrentUser();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        boolean isAssigned = authenticatedUserService.isAssignedToPatient(currentUser, patient);

        if (!isAssigned && !authenticatedUserService.isAdmin(currentUser)) {
            throw new UnauthorizedRoleException();
        }

        patient.unassignTherapist(currentUser);

        Patient updated = patientRepository.save(patient);

        return PatientMapper.toResponse(updated);
    }
}