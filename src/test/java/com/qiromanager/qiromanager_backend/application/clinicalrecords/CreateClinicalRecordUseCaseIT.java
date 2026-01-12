package com.qiromanager.qiromanager_backend.application.clinicalrecords;

import com.qiromanager.qiromanager_backend.api.clinicalrecords.ClinicalRecordResponse;
import com.qiromanager.qiromanager_backend.api.clinicalrecords.CreateClinicalRecordRequest;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.RecordType;
import com.qiromanager.qiromanager_backend.domain.exceptions.UnauthorizedRoleException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.storage.StoragePort;
import com.qiromanager.qiromanager_backend.domain.storage.StoredFile;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import com.qiromanager.qiromanager_backend.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CreateClinicalRecordUseCaseIT {

    @Autowired
    private CreateClinicalRecordUseCase createClinicalRecordUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    @MockitoBean
    private StoragePort storagePort;

    private User therapist;
    private Patient patient;

    @BeforeEach
    void setup() {
        therapist = User.create("Therapist John", "therapist", "therapist@test.com", "pass", Role.USER);
        userRepository.save(therapist);

        patient = Patient.create("Sick Patient", LocalDate.now(), "123", "sick@test.com", "Address", "Notes");
        patientRepository.save(patient);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("therapist", null, Collections.emptyList())
        );
    }

    @Test
    void shouldCreateRecord_WithAttachment_WhenUserIsAssigned() {
        patient.assignTherapist(therapist);
        patientRepository.save(patient);

        CreateClinicalRecordRequest request = new CreateClinicalRecordRequest();
        request.setType(RecordType.EVOLUTION);
        request.setContent("Patient reports feeling much better.");

        MockMultipartFile file = new MockMultipartFile(
                "file", "xray.pdf", "application/pdf", "dummy content".getBytes()
        );

        when(storagePort.upload(any())).thenReturn(
                StoredFile.builder()
                        .url("http://cloudinary.fake/xray.pdf")
                        .publicId("folder/xray_123")
                        .build()
        );

        ClinicalRecordResponse response = createClinicalRecordUseCase.execute(patient.getId(), request, file);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getContent()).isEqualTo("Patient reports feeling much better.");
        assertThat(response.getAttachments()).hasSize(1);
        assertThat(response.getAttachments().get(0).getUrl()).isEqualTo("http://cloudinary.fake/xray.pdf");

        ClinicalRecord savedRecord = clinicalRecordRepository.findById(response.getId()).orElseThrow();
        assertThat(savedRecord.getAttachments()).hasSize(1);
        assertThat(savedRecord.getAttachments().iterator().next().getPublicId()).isEqualTo("folder/xray_123");

        verify(storagePort, times(1)).upload(any());
    }

    @Test
    void shouldCreateRecord_WithoutAttachment() {
        patient.assignTherapist(therapist);
        patientRepository.save(patient);

        CreateClinicalRecordRequest request = new CreateClinicalRecordRequest();
        request.setType(RecordType.ANAMNESIS);
        request.setContent("Initial interview.");

        ClinicalRecordResponse response = createClinicalRecordUseCase.execute(patient.getId(), request, null);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getAttachments()).isEmpty();

        verify(storagePort, never()).upload(any());
    }

}