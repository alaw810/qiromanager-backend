package com.qiromanager.qiromanager_backend.infrastructure.clinicalhistory;

import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.RecordType;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClinicalRecordRepositoryIT {

    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void shouldSaveAndRetrieveRecordsWithAttachments_OrderedByDateDesc() throws InterruptedException {
        Patient patient = Patient.create(
                "History Test Patient",
                LocalDate.of(1990, 1, 1),
                "123456789",
                "history@test.com",
                "Address",
                "Notes"
        );
        patientRepository.save(patient);

        ClinicalRecord olderRecord = ClinicalRecord.create(
                patient,
                null,
                RecordType.ANAMNESIS,
                "First visit interview"
        );
        clinicalRecordRepository.save(olderRecord);

        Thread.sleep(100);

        ClinicalRecord newerRecord = ClinicalRecord.create(
                patient,
                null,
                RecordType.EVOLUTION,
                "Second visit evolution"
        );

        newerRecord.addAttachment(
                "http://cloud.url/xray.jpg",
                "folder/xray_123",
                "xray.jpg",
                "image/jpeg",
                2048L
        );

        clinicalRecordRepository.save(newerRecord);

        List<ClinicalRecord> history = clinicalRecordRepository.findByPatientId(patient.getId());

        assertThat(history).hasSize(2);

        assertThat(history.get(0).getType()).isEqualTo(RecordType.EVOLUTION);
        assertThat(history.get(0).getContent()).isEqualTo("Second visit evolution");

        assertThat(history.get(1).getType()).isEqualTo(RecordType.ANAMNESIS);
        assertThat(history.get(1).getContent()).isEqualTo("First visit interview");

        ClinicalRecord retrievedNewer = history.get(0);
        assertThat(retrievedNewer.getAttachments()).hasSize(1);

        var attachment = retrievedNewer.getAttachments().iterator().next();
        assertThat(attachment.getOriginalFilename()).isEqualTo("xray.jpg");
        assertThat(attachment.getPublicId()).isEqualTo("folder/xray_123");
    }
}