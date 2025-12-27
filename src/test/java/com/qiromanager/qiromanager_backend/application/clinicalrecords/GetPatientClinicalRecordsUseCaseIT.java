package com.qiromanager.qiromanager_backend.application.clinicalrecords;

import com.qiromanager.qiromanager_backend.api.clinicalrecords.ClinicalRecordResponse;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.RecordType;
import com.qiromanager.qiromanager_backend.domain.exceptions.UnauthorizedRoleException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GetPatientClinicalRecordsUseCaseIT {

    @Autowired
    private GetPatientClinicalRecordsUseCase getPatientClinicalRecordsUseCase;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    private User therapist;
    private Patient patient;

    @BeforeEach
    void setup() {
        therapist = User.create("Therapist Reader", "reader", "reader@test.com", "pass", Role.USER);
        userRepository.save(therapist);

        patient = Patient.create("Patient History", LocalDate.now(), "123", "hist@test.com", "Addr", "Note");
        patientRepository.save(patient);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("reader", null, Collections.emptyList())
        );
    }

    @Test
    void shouldReturnRecords_WhenUserAssigned() {
        patient.assignTherapist(therapist);
        patientRepository.save(patient);

        ClinicalRecord record1 = ClinicalRecord.create(patient, therapist, RecordType.ANAMNESIS, "First");
        ClinicalRecord record2 = ClinicalRecord.create(patient, therapist, RecordType.EVOLUTION, "Second");
        clinicalRecordRepository.save(record1);
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        clinicalRecordRepository.save(record2);

        List<ClinicalRecordResponse> responses = getPatientClinicalRecordsUseCase.execute(patient.getId());

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getContent()).isEqualTo("Second");
        assertThat(responses.get(1).getContent()).isEqualTo("First");
    }

    @Test
    void shouldThrowException_WhenUserNotAssigned() {
        assertThatThrownBy(() -> getPatientClinicalRecordsUseCase.execute(patient.getId()))
                .isInstanceOf(UnauthorizedRoleException.class);
    }
}