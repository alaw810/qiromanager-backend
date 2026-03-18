package com.qiromanager.qiromanager_backend.application.treatments;

import com.qiromanager.qiromanager_backend.domain.exceptions.TreatmentSessionNotFoundException;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteTreatmentSessionUseCase {

    private final TreatmentSessionRepository treatmentSessionRepository;

    @Transactional
    public void execute(Long patientId, Long sessionId) {
        TreatmentSession session = treatmentSessionRepository.findById(sessionId)
                .filter(s -> s.getPatient().getId().equals(patientId))
                .orElseThrow(() -> new TreatmentSessionNotFoundException(sessionId));

        treatmentSessionRepository.delete(session.getId());
        log.info("Treatment session deleted (ID: {})", sessionId);
    }
}
