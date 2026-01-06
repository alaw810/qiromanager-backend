package com.qiromanager.qiromanager_backend.domain.patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {

    Optional<Patient> findById(Long id);

    List<Patient> findAll();

    List<Patient> findAllActive();

    List<Patient> findActiveByTherapistId(Long therapistId);

    Patient save(Patient patient);

    List<Patient> searchByFullName(String query);

    long countAll();

    long countActive();

    long countByTherapistId(Long therapistId);
}