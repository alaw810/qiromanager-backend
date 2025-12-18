package com.qiromanager.qiromanager_backend.infrastructure.clinicalhistory;

import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.infrastructure.clinicalhistory.jpa.JpaClinicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClinicalRecordRepositoryImpl implements ClinicalRecordRepository {

    private final JpaClinicalRecordRepository jpaRepository;

    @Override
    public ClinicalRecord save(ClinicalRecord record) {
        return jpaRepository.save(record);
    }

    @Override
    public Optional<ClinicalRecord> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ClinicalRecord> findByPatientId(Long patientId) {
        return jpaRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}