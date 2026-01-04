package com.qiromanager.qiromanager_backend.infrastructure.patient;

import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.infrastructure.patient.jpa.JpaPatientRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepositoryImpl implements PatientRepository {

    private final JpaPatientRepository jpaRepository;

    public PatientRepositoryImpl(JpaPatientRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Patient> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<Patient> findAllActive() {
        return jpaRepository.findAllActive();
    }

    @Override
    public Patient save(Patient patient) {
        return jpaRepository.save(patient);
    }

    @Override
    public List<Patient> searchByFullName(String query) {
        return jpaRepository.searchByFullName(query);
    }

    @Override
    public long countAll() {
        return jpaRepository.count();
    }

    @Override
    public long countActive() {
        return jpaRepository.countByActiveTrue();
    }

    @Override
    public long countByTherapistId(Long therapistId) {
        return jpaRepository.countByTherapistId(therapistId);
    }
}
