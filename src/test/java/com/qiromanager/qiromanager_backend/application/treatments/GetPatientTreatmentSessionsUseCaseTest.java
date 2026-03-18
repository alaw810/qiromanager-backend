package com.qiromanager.qiromanager_backend.application.treatments;

import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSessionRepository;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPatientTreatmentSessionsUseCaseTest {

    @Mock
    private TreatmentSessionRepository treatmentSessionRepository;

    @InjectMocks
    private GetPatientTreatmentSessionsUseCase getPatientTreatmentSessionsUseCase;

    @Test
    void execute_shouldReturnSessions_forPatient() {
        Patient patient = Patient.create("John Patient", LocalDate.of(1990, 1, 1), null, null, null, null);
        User therapist = User.create("Jane", "janedoe", "jane@clinic.com", "pass", Role.USER);
        TreatmentSession session = TreatmentSession.create(patient, therapist, LocalDateTime.now(), "First session notes");

        when(treatmentSessionRepository.findByPatientId(1L)).thenReturn(List.of(session));

        List<TreatmentSession> result = getPatientTreatmentSessionsUseCase.execute(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNotes()).isEqualTo("First session notes");
    }

    @Test
    void execute_shouldReturnEmptyList_whenNoSessions() {
        when(treatmentSessionRepository.findByPatientId(99L)).thenReturn(Collections.emptyList());

        List<TreatmentSession> result = getPatientTreatmentSessionsUseCase.execute(99L);

        assertThat(result).isEmpty();
    }
}
