package com.qiromanager.qiromanager_backend.application.clinicalrecords;

import com.qiromanager.qiromanager_backend.api.clinicalrecords.ClinicalRecordResponse;
import com.qiromanager.qiromanager_backend.api.mappers.ClinicalRecordMapper;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
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
public class GetPatientClinicalRecordsUseCase {

    private final ClinicalRecordRepository clinicalRecordRepository;
    private final PatientRepository patientRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ClinicalRecordMapper mapper;

    @Transactional(readOnly = true)
    public List<ClinicalRecordResponse> execute(Long patientId) {
        User currentUser = authenticatedUserService.getCurrentUser();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        authenticatedUserService.assertCanAccessPatient(currentUser, patient);

        List<ClinicalRecord> records = clinicalRecordRepository.findByPatientId(patientId);

        return records.stream()
                .map(mapper::toResponse)
                .toList();
    }
}