package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.application.audit.AuditService;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignPatientUseCaseTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AssignPatientUseCase assignPatientUseCase;

    private User therapist;
    private Patient patient;

    @BeforeEach
    void setUp() {
        therapist = User.create("Jane Doe", "janedoe", "jane@clinic.com", "pass", Role.USER);
        therapist.forceId(1L);

        patient = Patient.create("John Patient", LocalDate.of(1990, 1, 1), null, null, null, null);
    }

    @Test
    void execute_shouldAssignTherapist_whenNotAlreadyAssigned() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(therapist);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(authenticatedUserService.isAssignedToPatient(therapist, patient)).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponse response = assignPatientUseCase.execute(1L);

        assertThat(response).isNotNull();
        assertThat(patient.getTherapists()).contains(therapist);
        verify(patientRepository).save(patient);
    }

    @Test
    void execute_shouldNotAddDuplicate_whenAlreadyAssigned() {
        patient.assignTherapist(therapist);

        when(authenticatedUserService.getCurrentUser()).thenReturn(therapist);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(authenticatedUserService.isAssignedToPatient(therapist, patient)).thenReturn(true);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assignPatientUseCase.execute(1L);

        // therapist was already assigned — should still be exactly once in the set
        assertThat(patient.getTherapists()).hasSize(1);
        verify(patientRepository).save(patient);
    }

    @Test
    void execute_shouldThrow_whenPatientNotFound() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(therapist);
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignPatientUseCase.execute(99L))
                .isInstanceOf(PatientNotFoundException.class);

        verify(patientRepository, never()).save(any());
    }
}
