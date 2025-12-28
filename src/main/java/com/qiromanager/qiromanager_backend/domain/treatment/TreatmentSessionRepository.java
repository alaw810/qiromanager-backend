package com.qiromanager.qiromanager_backend.domain.treatment;

import java.util.List;
import java.util.Optional;

public interface TreatmentSessionRepository {
    TreatmentSession save(TreatmentSession session);
    Optional<TreatmentSession> findById(Long id);
    List<TreatmentSession> findByPatientId(Long patientId);
}