package com.qiromanager.qiromanager_backend.api.mappers;

import com.qiromanager.qiromanager_backend.api.clinicalrecords.AttachmentDto;
import com.qiromanager.qiromanager_backend.api.clinicalrecords.ClinicalRecordResponse;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.Attachment;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClinicalRecordMapper {

    public ClinicalRecordResponse toResponse(ClinicalRecord record) {
        return ClinicalRecordResponse.builder()
                .id(record.getId())
                .patientId(record.getPatient().getId())
                .performedByName(record.getPerformedBy() != null ?
                        record.getPerformedBy().getFullName() :
                        "Unknown")
                .type(record.getType())
                .content(record.getContent())
                .createdAt(record.getCreatedAt())
                .attachments(toAttachmentDtoList(record))
                .build();
    }

    private List<AttachmentDto> toAttachmentDtoList(ClinicalRecord record) {
        return record.getAttachments().stream()
                .map(this::toAttachmentDto)
                .collect(Collectors.toList());
    }

    private AttachmentDto toAttachmentDto(Attachment attachment) {
        return AttachmentDto.builder()
                .id(attachment.getId())
                .url(attachment.getUrl())
                .originalFilename(attachment.getOriginalFilename())
                .mimeType(attachment.getMimeType())
                .size(attachment.getSize())
                .build();
    }
}