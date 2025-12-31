package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.mappers.PatientMapper;
import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.api.patients.UpdatePatientRequest;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePatientUseCase {

    private final PatientRepository patientRepository;

    @Transactional
    public PatientResponse execute(Long id, UpdatePatientRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

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