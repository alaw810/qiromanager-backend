package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.mappers.PatientMapper;
import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListPatientsUseCase {

    private final PatientRepository patientRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional(readOnly = true)
    public List<PatientResponse> execute(Boolean assignedToMe) {

        User currentUser = authenticatedUserService.getCurrentUser();
        List<Patient> patients;

        boolean shouldFilterByTherapist = !authenticatedUserService.isAdmin(currentUser)
                || Boolean.TRUE.equals(assignedToMe);

        if (shouldFilterByTherapist) {
            patients = patientRepository.findActiveByTherapistId(currentUser.getId());
        } else {
            patients = patientRepository.findAllActive();
        }

        return patients.stream()
                .map(PatientMapper::toResponse)
                .toList();
    }
}