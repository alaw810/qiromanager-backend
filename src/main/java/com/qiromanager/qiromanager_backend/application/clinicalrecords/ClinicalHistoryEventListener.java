package com.qiromanager.qiromanager_backend.application.clinicalrecords;

import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.RecordType;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import com.qiromanager.qiromanager_backend.domain.treatment.events.TreatmentSessionCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ClinicalHistoryEventListener {

    private final ClinicalRecordRepository clinicalRecordRepository;

    @EventListener
    @Transactional
    public void handleTreatmentSessionCreated(TreatmentSessionCreatedEvent event) {
        TreatmentSession session = event.getTreatmentSession();

        ClinicalRecord historyEntry = ClinicalRecord.create(
                session.getPatient(),
                session.getTherapist(),
                RecordType.EVOLUTION,
                session.generateClinicalSummary()
        );

        clinicalRecordRepository.save(historyEntry);
    }
}