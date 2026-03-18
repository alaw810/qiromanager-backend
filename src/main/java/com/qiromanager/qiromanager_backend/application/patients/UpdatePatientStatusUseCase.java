package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.mappers.PatientMapper;
import com.qiromanager.qiromanager_backend.api.patients.UpdatePatientStatusRequest;
import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.application.audit.AuditService;
import com.qiromanager.qiromanager_backend.domain.audit.AuditAction;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdatePatientStatusUseCase {

    private final PatientRepository patientRepository;
    private final AuditService auditService;

    @Transactional
    @CacheEvict(value = "patients", key = "#id")
    public PatientResponse execute(Long id, UpdatePatientStatusRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        AuditAction action;
        if (Boolean.TRUE.equals(request.getActive())) {
            patient.activate();
            action = AuditAction.PATIENT_ACTIVATED;
            log.info("Patient ID: {} status changed to ACTIVE", id);
        } else {
            patient.deactivate();
            action = AuditAction.PATIENT_DEACTIVATED;
            log.info("Patient ID: {} status changed to INACTIVE", id);
        }

        Patient updated = patientRepository.save(patient);
        auditService.log("Patient", id, action, null);

        return PatientMapper.toResponse(updated);
    }
}
