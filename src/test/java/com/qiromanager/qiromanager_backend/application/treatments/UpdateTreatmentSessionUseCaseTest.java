package com.qiromanager.qiromanager_backend.application.treatments;

import com.qiromanager.qiromanager_backend.api.mappers.TreatmentSessionMapper;
import com.qiromanager.qiromanager_backend.api.treatments.TreatmentSessionResponse;
import com.qiromanager.qiromanager_backend.api.treatments.UpdateTreatmentSessionRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTreatmentSessionUseCaseTest {

    @Mock
    private TreatmentSessionRepository treatmentSessionRepository;

    @Mock
    private TreatmentSessionMapper mapper;

    @InjectMocks
    private UpdateTreatmentSessionUseCase updateTreatmentSessionUseCase;

    private Patient patient;
    private User therapist;
    private TreatmentSession session;

    @BeforeEach
    void setUp() {
        therapist = User.create("Jane Doe", "janedoe", "jane@clinic.com", "pass", Role.USER);
        therapist.forceId(1L);

        patient = Patient.create("John Patient", LocalDate.of(1990, 1, 1), null, null, null, null);
        patient.forceId(10L);

        session = TreatmentSession.create(patient, therapist, LocalDateTime.now().minusDays(1), "Old notes");
        session.forceId(100L);
    }

    @Test
    void execute_shouldUpdateSession_whenFoundAndBelongsToPatient() {
        LocalDateTime newDate = LocalDateTime.now();
        UpdateTreatmentSessionRequest request = new UpdateTreatmentSessionRequest();
        request.setSessionDate(newDate);
        request.setNotes("Updated notes");

        TreatmentSessionResponse expectedResponse = TreatmentSessionResponse.builder()
                .id(100L)
                .patientId(10L)
                .therapistName("Jane Doe")
                .sessionDate(newDate)
                .notes("Updated notes")
                .createdAt(session.getCreatedAt())
                .build();

        when(treatmentSessionRepository.findById(100L)).thenReturn(Optional.of(session));
        when(treatmentSessionRepository.save(any(TreatmentSession.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(session)).thenReturn(expectedResponse);

        TreatmentSessionResponse response = updateTreatmentSessionUseCase.execute(10L, 100L, request);

        assertThat(response).isNotNull();
        assertThat(response.getNotes()).isEqualTo("Updated notes");
        assertThat(session.getNotes()).isEqualTo("Updated notes");
        assertThat(session.getSessionDate()).isEqualTo(newDate);
        verify(treatmentSessionRepository).save(session);
    }

    @Test
    void execute_shouldThrow_whenSessionNotFound() {
        when(treatmentSessionRepository.findById(999L)).thenReturn(Optional.empty());

        UpdateTreatmentSessionRequest request = new UpdateTreatmentSessionRequest();
        request.setSessionDate(LocalDateTime.now());
        request.setNotes("Notes");

        assertThatThrownBy(() -> updateTreatmentSessionUseCase.execute(10L, 999L, request))
                .isInstanceOf(TreatmentSessionNotFoundException.class);

        verify(treatmentSessionRepository, never()).save(any());
    }

    @Test
    void execute_shouldThrow_whenSessionDoesNotBelongToPatient() {
        when(treatmentSessionRepository.findById(100L)).thenReturn(Optional.of(session));

        UpdateTreatmentSessionRequest request = new UpdateTreatmentSessionRequest();
        request.setSessionDate(LocalDateTime.now());
        request.setNotes("Notes");

        // session belongs to patient 10, but we query for patient 99
        assertThatThrownBy(() -> updateTreatmentSessionUseCase.execute(99L, 100L, request))
                .isInstanceOf(TreatmentSessionNotFoundException.class);

        verify(treatmentSessionRepository, never()).save(any());
    }
}
