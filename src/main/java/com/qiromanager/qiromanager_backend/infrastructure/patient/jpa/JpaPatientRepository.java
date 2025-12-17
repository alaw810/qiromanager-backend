package com.qiromanager.qiromanager_backend.infrastructure.patient.jpa;

import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaPatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT p FROM Patient p WHERE p.active = true")
    List<Patient> findAllActive();

    @Query("SELECT p FROM Patient p WHERE p.active = true AND LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Patient> searchByFullName(@Param("query") String query);

}
