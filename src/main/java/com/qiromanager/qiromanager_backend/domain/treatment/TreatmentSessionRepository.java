package com.qiromanager.qiromanager_backend.domain.treatment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TreatmentSessionRepository {
    TreatmentSession save(TreatmentSession session);
    Optional<TreatmentSession> findById(Long id);
    List<TreatmentSession> findByPatientId(Long patientId);
    long countSessionsBetween(LocalDateTime start, LocalDateTime end);
    long countTherapistSessionsBetween(Long therapistId, LocalDateTime start, LocalDateTime end);
}