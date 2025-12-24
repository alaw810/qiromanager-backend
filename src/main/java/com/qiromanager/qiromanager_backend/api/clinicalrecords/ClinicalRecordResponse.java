package com.qiromanager.qiromanager_backend.api.clinicalrecords;

import com.qiromanager.qiromanager_backend.domain.clinicalhistory.RecordType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ClinicalRecordResponse {
    private Long id;
    private Long patientId;
    private String performedByName;
    private RecordType type;
    private String content;
    private List<AttachmentDto> attachments;
    private LocalDateTime createdAt;
}