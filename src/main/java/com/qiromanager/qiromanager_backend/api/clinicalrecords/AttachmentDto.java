package com.qiromanager.qiromanager_backend.api.clinicalrecords;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttachmentDto {
    private Long id;
    private String url;
    private String originalFilename;
    private String mimeType;
    private Long size;
}