package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.mappers.PatientMapper;
import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListPatientsUseCase {

    private final PatientRepository patientRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional(readOnly = true)
    public Page<PatientResponse> execute(Boolean assignedToMe, Pageable pageable) {

        User currentUser = authenticatedUserService.getCurrentUser();

        boolean shouldFilterByTherapist = !authenticatedUserService.isAdmin(currentUser)
                || Boolean.TRUE.equals(assignedToMe);

        if (shouldFilterByTherapist) {
            return patientRepository.findActiveByTherapistId(currentUser.getId(), pageable)
                    .map(PatientMapper::toResponse);
        } else {
            return patientRepository.findAllActive(pageable)
                    .map(PatientMapper::toResponse);
        }
    }
}
