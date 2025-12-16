package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.mappers.PatientMapper;
import com.qiromanager.qiromanager_backend.api.patients.CreatePatientRequest;
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
public class CreatePatientUseCase {

    private final PatientRepository patientRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public PatientResponse execute(CreatePatientRequest request) {

        User therapist = authenticatedUserService.getCurrentUser();

        Patient patient = Patient.create(
                request.getFullName(),
                request.getDateOfBirth(),
                request.getPhone(),
                request.getEmail(),
                request.getAddress(),
                request.getGeneralNotes()
        );

        patient.assignTherapist(therapist);

        Patient saved = patientRepository.save(patient);

        return PatientMapper.toResponse(saved);
    }
}
