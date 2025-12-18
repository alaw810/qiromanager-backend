package com.qiromanager.qiromanager_backend.domain.clinicalhistory;

import java.util.List;
import java.util.Optional;

public interface ClinicalRecordRepository {

    ClinicalRecord save(ClinicalRecord record);

    Optional<ClinicalRecord> findById(Long id);

    List<ClinicalRecord> findByPatientId(Long patientId);

    void deleteById(Long id);
}