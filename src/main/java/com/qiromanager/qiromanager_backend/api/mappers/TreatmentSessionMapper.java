package com.qiromanager.qiromanager_backend.api.mappers;

import com.qiromanager.qiromanager_backend.api.treatments.TreatmentSessionResponse;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import org.springframework.stereotype.Component;

@Component
public class TreatmentSessionMapper {

    public TreatmentSessionResponse toResponse(TreatmentSession session) {
        return TreatmentSessionResponse.builder()
                .id(session.getId())
                .patientId(session.getPatient().getId())
                .therapistName(session.getTherapist().getFullName())
                .sessionDate(session.getSessionDate())
                .notes(session.getNotes())
                .createdAt(session.getCreatedAt())
                .build();
    }
}