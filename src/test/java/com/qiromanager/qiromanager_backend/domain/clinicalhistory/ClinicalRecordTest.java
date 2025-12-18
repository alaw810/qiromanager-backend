package com.qiromanager.qiromanager_backend.domain.clinicalhistory;

import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ClinicalRecordTest {

    @Mock
    private Patient patient;

    @Mock
    private User therapist;

    @Test
    void create_shouldReturnValidInstance_whenDataIsCorrect() {
        ClinicalRecord record = ClinicalRecord.create(
                patient,
                therapist,
                RecordType.EVOLUTION,
                "Patient reports improvement in lower back pain."
        );

        assertThat(record).isNotNull();
        assertThat(record.getPatient()).isEqualTo(patient);
        assertThat(record.getPerformedBy()).isEqualTo(therapist);
        assertThat(record.getType()).isEqualTo(RecordType.EVOLUTION);
        assertThat(record.getContent()).contains("improvement");
        assertThat(record.getAttachments()).isEmpty();
        assertThat(record.getCreatedAt()).isNotNull();
    }

    @Test
    void create_shouldThrowException_whenPatientIsNull() {
        assertThatThrownBy(() -> ClinicalRecord.create(
                null,
                therapist,
                RecordType.EVOLUTION,
                "Content"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Clinical record must be assigned to a patient");
    }

    @Test
    void create_shouldThrowException_whenTypeIsNull() {
        assertThatThrownBy(() -> ClinicalRecord.create(
                patient,
                therapist,
                null,
                "Content"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Record type is required");
    }

    @Test
    void create_shouldThrowException_whenContentIsEmpty() {
        assertThatThrownBy(() -> ClinicalRecord.create(
                patient,
                therapist,
                RecordType.EVOLUTION,
                "   "
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content cannot be empty");
    }

    @Test
    void addAttachment_shouldAddAttachmentToSet() {
        ClinicalRecord record = ClinicalRecord.create(patient, therapist, RecordType.EVOLUTION, "Note");

        record.addAttachment("http://cloud.url/file.pdf", "file.pdf", "application/pdf", 1024L);

        assertThat(record.getAttachments()).hasSize(1);
        Attachment attachment = record.getAttachments().iterator().next();
        assertThat(attachment.getUrl()).isEqualTo("http://cloud.url/file.pdf");
        assertThat(attachment.getClinicalRecord()).isEqualTo(record);
    }
}