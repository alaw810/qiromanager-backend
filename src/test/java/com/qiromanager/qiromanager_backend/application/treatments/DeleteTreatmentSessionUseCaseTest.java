package com.qiromanager.qiromanager_backend.application.treatments;

import com.qiromanager.qiromanager_backend.domain.exceptions.TreatmentSessionNotFoundException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSession;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSessionRepository;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTreatmentSessionUseCaseTest {

    @Mock
    private TreatmentSessionRepository treatmentSessionRepository;

    @InjectMocks
    private DeleteTreatmentSessionUseCase deleteTreatmentSessionUseCase;

    private Patient patient;
    private User therapist;
    private TreatmentSession session;

    @BeforeEach
    void setUp() {
        therapist = User.create("Jane Doe", "janedoe", "jane@clinic.com", "pass", Role.USER);
        therapist.forceId(1L);

        patient = Patient.create("John Patient", LocalDate.of(1990, 1, 1), null, null, null, null);
        patient.forceId(10L);

        session = TreatmentSession.create(patient, therapist, LocalDateTime.now(), "Some notes");
        session.forceId(100L);
    }

    @Test
    void execute_shouldDeleteSession_whenFoundAndBelongsToPatient() {
        when(treatmentSessionRepository.findById(100L)).thenReturn(Optional.of(session));

        deleteTreatmentSessionUseCase.execute(10L, 100L);

        verify(treatmentSessionRepository).delete(100L);
    }

    @Test
    void execute_shouldThrow_whenSessionNotFound() {
        when(treatmentSessionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteTreatmentSessionUseCase.execute(10L, 999L))
                .isInstanceOf(TreatmentSessionNotFoundException.class);

        verify(treatmentSessionRepository, never()).delete(any());
    }

    @Test
    void execute_shouldThrow_whenSessionDoesNotBelongToPatient() {
        when(treatmentSessionRepository.findById(100L)).thenReturn(Optional.of(session));

        // session belongs to patient 10, but we query for patient 99
        assertThatThrownBy(() -> deleteTreatmentSessionUseCase.execute(99L, 100L))
                .isInstanceOf(TreatmentSessionNotFoundException.class);

        verify(treatmentSessionRepository, never()).delete(any());
    }
}
