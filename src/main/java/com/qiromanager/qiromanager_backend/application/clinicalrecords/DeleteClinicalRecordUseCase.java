package com.qiromanager.qiromanager_backend.application.clinicalrecords;

import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.domain.exceptions.ClinicalRecordNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteClinicalRecordUseCase {

    private final ClinicalRecordRepository clinicalRecordRepository;

    @Transactional
    public void execute(Long patientId, Long recordId) {
        ClinicalRecord record = clinicalRecordRepository.findById(recordId)
                .filter(r -> r.getPatient().getId().equals(patientId))
                .orElseThrow(() -> new ClinicalRecordNotFoundException(recordId));

        clinicalRecordRepository.deleteById(record.getId());
        log.info("Clinical record deleted (ID: {})", recordId);
    }
}
