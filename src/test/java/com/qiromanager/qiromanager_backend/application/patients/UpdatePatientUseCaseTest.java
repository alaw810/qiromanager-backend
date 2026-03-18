package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.api.patients.UpdatePatientRequest;
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
class UpdatePatientUseCaseTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private UpdatePatientUseCase updatePatientUseCase;

    @Test
    void execute_shouldUpdatePatient_whenExists() {
        Patient patient = Patient.create("Old Name", LocalDate.of(1990, 1, 1), null, null, null, null);

        UpdatePatientRequest request = new UpdatePatientRequest();
        request.setFullName("New Name");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setEmail("new@email.com");
        request.setPhone("600111222");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponse response = updatePatientUseCase.execute(1L, request);

        assertThat(response.getFullName()).isEqualTo("New Name");
        assertThat(response.getEmail()).isEqualTo("new@email.com");
        verify(patientRepository).save(patient);
    }

    @Test
    void execute_shouldThrow_whenPatientNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        UpdatePatientRequest request = new UpdatePatientRequest();
        request.setFullName("Name");

        assertThatThrownBy(() -> updatePatientUseCase.execute(99L, request))
                .isInstanceOf(PatientNotFoundException.class);

        verify(patientRepository, never()).save(any());
    }
}
