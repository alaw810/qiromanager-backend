package com.qiromanager.qiromanager_backend.application.clinicalrecords;

import com.qiromanager.qiromanager_backend.api.clinicalrecords.ClinicalRecordResponse;
import com.qiromanager.qiromanager_backend.api.mappers.ClinicalRecordMapper;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPatientClinicalRecordsUseCase {

    private final ClinicalRecordRepository clinicalRecordRepository;
    private final ClinicalRecordMapper mapper;

    @Transactional(readOnly = true)
    public List<ClinicalRecordResponse> execute(Long patientId) {
        List<ClinicalRecord> records = clinicalRecordRepository.findByPatientId(patientId);

        return records.stream()
                .map(mapper::toResponse)
                .toList();
    }
}