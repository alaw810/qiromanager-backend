package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.api.patients.UpdatePatientStatusRequest;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
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
class UpdatePatientStatusUseCaseTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private UpdatePatientStatusUseCase updatePatientStatusUseCase;

    @Test
    void execute_shouldActivatePatient_whenRequestIsTrue() {
        Patient patient = Patient.create("John Patient", LocalDate.of(1990, 1, 1), null, null, null, null);
        patient.deactivate();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdatePatientStatusRequest request = new UpdatePatientStatusRequest();
        request.setActive(true);

        PatientResponse response = updatePatientStatusUseCase.execute(1L, request);

        assertThat(response.isActive()).isTrue();
        verify(patientRepository).save(patient);
    }

    @Test
    void execute_shouldDeactivatePatient_whenRequestIsFalse() {
        Patient patient = Patient.create("John Patient", LocalDate.of(1990, 1, 1), null, null, null, null);

        assertThat(patient.isActive()).isTrue();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdatePatientStatusRequest request = new UpdatePatientStatusRequest();
        request.setActive(false);

        PatientResponse response = updatePatientStatusUseCase.execute(1L, request);

        assertThat(response.isActive()).isFalse();
        verify(patientRepository).save(patient);
    }

    @Test
    void execute_shouldThrow_whenPatientNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        UpdatePatientStatusRequest request = new UpdatePatientStatusRequest();
        request.setActive(true);

        assertThatThrownBy(() -> updatePatientStatusUseCase.execute(99L, request))
                .isInstanceOf(PatientNotFoundException.class);

        verify(patientRepository, never()).save(any());
    }
}
