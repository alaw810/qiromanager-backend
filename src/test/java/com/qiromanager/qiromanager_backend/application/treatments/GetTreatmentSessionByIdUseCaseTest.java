package com.qiromanager.qiromanager_backend.application.treatments;

import com.qiromanager.qiromanager_backend.api.mappers.TreatmentSessionMapper;
import com.qiromanager.qiromanager_backend.api.treatments.TreatmentSessionResponse;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTreatmentSessionByIdUseCaseTest {

    @Mock
    private TreatmentSessionRepository treatmentSessionRepository;

    @Mock
    private TreatmentSessionMapper mapper;

    @InjectMocks
    private GetTreatmentSessionByIdUseCase getTreatmentSessionByIdUseCase;

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
    void execute_shouldReturnSession_whenFoundAndBelongsToPatient() {
        TreatmentSessionResponse expectedResponse = TreatmentSessionResponse.builder()
                .id(100L)
                .patientId(10L)
                .therapistName("Jane Doe")
                .sessionDate(session.getSessionDate())
                .notes("Some notes")
                .createdAt(session.getCreatedAt())
                .build();

        when(treatmentSessionRepository.findById(100L)).thenReturn(Optional.of(session));
        when(mapper.toResponse(session)).thenReturn(expectedResponse);

        TreatmentSessionResponse response = getTreatmentSessionByIdUseCase.execute(10L, 100L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getPatientId()).isEqualTo(10L);
    }

    @Test
    void execute_shouldThrow_whenSessionNotFound() {
        when(treatmentSessionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getTreatmentSessionByIdUseCase.execute(10L, 999L))
                .isInstanceOf(TreatmentSessionNotFoundException.class);
    }

    @Test
    void execute_shouldThrow_whenSessionDoesNotBelongToPatient() {
        when(treatmentSessionRepository.findById(100L)).thenReturn(Optional.of(session));

        // session belongs to patient 10, but we query for patient 99
        assertThatThrownBy(() -> getTreatmentSessionByIdUseCase.execute(99L, 100L))
                .isInstanceOf(TreatmentSessionNotFoundException.class);
    }
}
