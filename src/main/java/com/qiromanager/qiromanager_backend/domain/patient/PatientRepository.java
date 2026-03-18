package com.qiromanager.qiromanager_backend.domain.patient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {

    Optional<Patient> findById(Long id);

    List<Patient> findAll();

    List<Patient> findAllActive();

    Page<Patient> findAllActive(Pageable pageable);

    List<Patient> findActiveByTherapistId(Long therapistId);

    Page<Patient> findActiveByTherapistId(Long therapistId, Pageable pageable);

    Patient save(Patient patient);

    List<Patient> searchByFullName(String query);

    Page<Patient> searchByFullName(String query, Pageable pageable);

    long countAll();

    long countActive();

    long countByTherapistId(Long therapistId);
}
