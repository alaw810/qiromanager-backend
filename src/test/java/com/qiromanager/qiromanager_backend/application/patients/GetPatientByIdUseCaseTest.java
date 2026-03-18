package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPatientByIdUseCaseTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private GetPatientByIdUseCase getPatientByIdUseCase;

    @Test
    void execute_shouldReturnPatient_whenExists() {
        Patient patient = Patient.create("John Patient", LocalDate.of(1990, 1, 1), "600111222", "john@test.com", null, null);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientResponse response = getPatientByIdUseCase.execute(1L);

        assertThat(response.getFullName()).isEqualTo("John Patient");
        assertThat(response.getEmail()).isEqualTo("john@test.com");
    }

    @Test
    void execute_shouldThrow_whenPatientNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getPatientByIdUseCase.execute(99L))
                .isInstanceOf(PatientNotFoundException.class);
    }
}
