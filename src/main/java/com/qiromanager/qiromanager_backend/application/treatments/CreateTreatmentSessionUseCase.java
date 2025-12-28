package com.qiromanager.qiromanager_backend.application.treatments;

import com.qiromanager.qiromanager_backend.api.mappers.TreatmentSessionMapper;
import com.qiromanager.qiromanager_backend.api.treatments.CreateTreatmentSessionRequest;
import com.qiromanager.qiromanager_backend.api.treatments.TreatmentSessionResponse;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSessionRepository;
import com.qiromanager.qiromanager_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateTreatmentSessionUseCase {

    private final TreatmentSessionRepository treatmentSessionRepository;
    private final PatientRepository patientRepository;
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

        return mapper.toResponse(treatmentSessionRepository.save(session));
    }
}