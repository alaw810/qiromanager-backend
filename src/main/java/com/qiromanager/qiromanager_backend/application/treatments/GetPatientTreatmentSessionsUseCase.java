package com.qiromanager.qiromanager_backend.application.treatments;

import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPatientTreatmentSessionsUseCase {

    private final TreatmentSessionRepository treatmentSessionRepository;

    @Transactional(readOnly = true)
    public List<TreatmentSession> execute(Long patientId) {
        return treatmentSessionRepository.findByPatientId(patientId);
    }
}