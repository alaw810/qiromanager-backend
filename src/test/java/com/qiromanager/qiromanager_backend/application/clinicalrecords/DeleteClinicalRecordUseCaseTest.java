package com.qiromanager.qiromanager_backend.application.clinicalrecords;

import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.RecordType;
import com.qiromanager.qiromanager_backend.domain.exceptions.ClinicalRecordNotFoundException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteClinicalRecordUseCaseTest {

    @Mock
    private ClinicalRecordRepository clinicalRecordRepository;

    @InjectMocks
    private DeleteClinicalRecordUseCase deleteClinicalRecordUseCase;

    private Patient patient;
    private User therapist;
    private ClinicalRecord record;

    @BeforeEach
    void setUp() {
        therapist = User.create("Jane Doe", "janedoe", "jane@clinic.com", "pass", Role.USER);
        therapist.forceId(1L);

        patient = Patient.create("John Patient", LocalDate.of(1990, 1, 1), null, null, null, null);
        patient.forceId(10L);

        record = ClinicalRecord.create(patient, therapist, RecordType.ANAMNESIS, "Record content");
        record.forceId(50L);
    }

    @Test
    void execute_shouldDeleteRecord_whenFoundAndBelongsToPatient() {
        when(clinicalRecordRepository.findById(50L)).thenReturn(Optional.of(record));

        deleteClinicalRecordUseCase.execute(10L, 50L);

        verify(clinicalRecordRepository).deleteById(50L);
    }

    @Test
    void execute_shouldThrow_whenRecordNotFound() {
        when(clinicalRecordRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteClinicalRecordUseCase.execute(10L, 999L))
                .isInstanceOf(ClinicalRecordNotFoundException.class);

        verify(clinicalRecordRepository, never()).deleteById(any());
    }

    @Test
    void execute_shouldThrow_whenRecordDoesNotBelongToPatient() {
        when(clinicalRecordRepository.findById(50L)).thenReturn(Optional.of(record));

        // record belongs to patient 10, but we query for patient 99
        assertThatThrownBy(() -> deleteClinicalRecordUseCase.execute(99L, 50L))
                .isInstanceOf(ClinicalRecordNotFoundException.class);

        verify(clinicalRecordRepository, never()).deleteById(any());
    }
}
