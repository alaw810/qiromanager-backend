package com.qiromanager.qiromanager_backend.infrastructure.treatment;

import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSessionRepository;
import com.qiromanager.qiromanager_backend.infrastructure.treatment.jpa.JpaTreatmentSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TreatmentSessionRepositoryImpl implements TreatmentSessionRepository {

    private final JpaTreatmentSessionRepository jpaRepository;

    @Override
    public TreatmentSession save(TreatmentSession session) {
        return jpaRepository.save(session);
    }

    @Override
    public Optional<TreatmentSession> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<TreatmentSession> findByPatientId(Long patientId) {
        return jpaRepository.findByPatientIdOrderBySessionDateDesc(patientId);
    }
}