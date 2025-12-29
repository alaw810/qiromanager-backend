package com.qiromanager.qiromanager_backend.application.treatments;

import com.qiromanager.qiromanager_backend.api.treatments.CreateTreatmentSessionRequest;
import com.qiromanager.qiromanager_backend.api.treatments.TreatmentSessionResponse;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.domain.exceptions.UnauthorizedRoleException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSessionRepository;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CreateTreatmentSessionUseCaseIT {

    @Autowired
    private CreateTreatmentSessionUseCase createTreatmentSessionUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TreatmentSessionRepository treatmentSessionRepository;

    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    private User therapist;
    private Patient patient;

    @BeforeEach
    void setup() {
        therapist = User.create("Physio Dave", "physio", "physio@test.com", "pass", Role.USER);
        userRepository.save(therapist);

        patient = Patient.create("Back Pain Patient", LocalDate.now(), "555-1234", "patient@test.com", "Address", "Notes");
        patientRepository.save(patient);
        
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("physio", null, Collections.emptyList())
        );
    }

    @Test
    void shouldCreateSession_AndHistoryEntry_WhenUserIsAssigned() {
        patient.assignTherapist(therapist);
        patientRepository.save(patient);

        CreateTreatmentSessionRequest request = new CreateTreatmentSessionRequest();
        request.setSessionDate(LocalDateTime.now().minusHours(1));
        request.setNotes("Manual therapy and dry needling.");

        TreatmentSessionResponse response = createTreatmentSessionUseCase.execute(patient.getId(), request);

        assertThat(response.getId()).isNotNull();
        TreatmentSession savedSession = treatmentSessionRepository.findById(response.getId()).orElseThrow();
        assertThat(savedSession.getNotes()).isEqualTo("Manual therapy and dry needling.");

        List<ClinicalRecord> history = clinicalRecordRepository.findByPatientId(patient.getId());
        assertThat(history).hasSize(1);

        ClinicalRecord autoEntry = history.get(0);
        assertThat(autoEntry.getContent()).contains("Treatment Session performed");
        assertThat(autoEntry.getContent()).contains("Manual therapy and dry needling.");
        assertThat(autoEntry.getPerformedBy()).isEqualTo(therapist);
    }

    @Test
    void shouldThrowException_WhenUserNotAssigned() {
        CreateTreatmentSessionRequest request = new CreateTreatmentSessionRequest();
        request.setSessionDate(LocalDateTime.now());
        request.setNotes("Trying to register a session without permission.");

        assertThatThrownBy(() -> createTreatmentSessionUseCase.execute(patient.getId(), request))
                .isInstanceOf(UnauthorizedRoleException.class);

        assertThat(treatmentSessionRepository.findByPatientId(patient.getId())).isEmpty();
    }
}