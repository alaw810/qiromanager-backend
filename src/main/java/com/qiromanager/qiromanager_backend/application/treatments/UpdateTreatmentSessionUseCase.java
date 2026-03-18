package com.qiromanager.qiromanager_backend.application.treatments;

import com.qiromanager.qiromanager_backend.api.mappers.TreatmentSessionMapper;
import com.qiromanager.qiromanager_backend.api.treatments.TreatmentSessionResponse;
import com.qiromanager.qiromanager_backend.api.treatments.UpdateTreatmentSessionRequest;
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
public class UpdateTreatmentSessionUseCase {

    private final TreatmentSessionRepository treatmentSessionRepository;
    private final TreatmentSessionMapper mapper;

    @Transactional
    public TreatmentSessionResponse execute(Long patientId, Long sessionId, UpdateTreatmentSessionRequest request) {
        TreatmentSession session = treatmentSessionRepository.findById(sessionId)
                .filter(s -> s.getPatient().getId().equals(patientId))
                .orElseThrow(() -> new TreatmentSessionNotFoundException(sessionId));

        session.update(request.getSessionDate(), request.getNotes());
        TreatmentSession saved = treatmentSessionRepository.save(session);

        return mapper.toResponse(saved);
    }
}
