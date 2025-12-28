package com.qiromanager.qiromanager_backend.infrastructure.treatment.jpa;

import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaTreatmentSessionRepository extends JpaRepository<TreatmentSession, Long> {
    List<TreatmentSession> findByPatientIdOrderBySessionDateDesc(Long patientId);
}