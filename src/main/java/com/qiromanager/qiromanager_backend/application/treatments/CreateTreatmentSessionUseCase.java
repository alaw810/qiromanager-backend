package com.qiromanager.qiromanager_backend.application.treatments;

import com.qiromanager.qiromanager_backend.api.mappers.TreatmentSessionMapper;
import com.qiromanager.qiromanager_backend.api.treatments.CreateTreatmentSessionRequest;
import com.qiromanager.qiromanager_backend.api.treatments.TreatmentSessionResponse;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.RecordType;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSessionRepository;
import com.qiromanager.qiromanager_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CreateTreatmentSessionUseCase {

    private final TreatmentSessionRepository treatmentSessionRepository;
    private final PatientRepository patientRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final TreatmentSessionMapper mapper;

    @Transactional
    public TreatmentSessionResponse execute(Long patientId, CreateTreatmentSessionRequest request) {
        User therapist = authenticatedUserService.getCurrentUser();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        authenticatedUserService.assertCanAccessPatient(therapist, patient);

        TreatmentSession session = TreatmentSession.create(
                patient,
                therapist,
                request.getSessionDate(),
                request.getNotes()
        );
        TreatmentSession savedSession = treatmentSessionRepository.save(session);

        createAutomaticHistoryEntry(savedSession);

        return mapper.toResponse(savedSession);
    }

    private void createAutomaticHistoryEntry(TreatmentSession session) {
        String formattedDate = session.getSessionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("Treatment Session performed on ").append(formattedDate);

        if (session.getNotes() != null && !session.getNotes().isBlank()) {
            contentBuilder.append("\nSession Notes: ").append(session.getNotes());
        }

        ClinicalRecord historyEntry = ClinicalRecord.create(
                session.getPatient(),
                session.getTherapist(),
                RecordType.EVOLUTION,
                contentBuilder.toString()
        );

        clinicalRecordRepository.save(historyEntry);
    }
}