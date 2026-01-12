package com.qiromanager.qiromanager_backend.infrastructure.clinicalhistory.jpa;

import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaClinicalRecordRepository extends JpaRepository<ClinicalRecord, Long> {

    List<ClinicalRecord> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}