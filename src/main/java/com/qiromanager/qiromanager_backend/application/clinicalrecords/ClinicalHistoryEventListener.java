package com.qiromanager.qiromanager_backend.application.clinicalrecords;

import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.RecordType;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import com.qiromanager.qiromanager_backend.domain.treatment.events.TreatmentSessionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClinicalHistoryEventListener {

    private final ClinicalRecordRepository clinicalRecordRepository;

    @EventListener
    @Transactional
    public void handleTreatmentSessionCreated(TreatmentSessionCreatedEvent event) {
        log.info("Event received: Generating clinical record from Treatment Session ID: {}", event.getTreatmentSession().getId());

        TreatmentSession session = event.getTreatmentSession();

        ClinicalRecord historyEntry = ClinicalRecord.create(
                session.getPatient(),
                session.getTherapist(),
                RecordType.EVOLUTION,
                session.generateClinicalSummary()
        );

        clinicalRecordRepository.save(historyEntry);

        log.debug("Auto-generated clinical record saved for Patient ID: {}", session.getPatient().getId());
    }
}