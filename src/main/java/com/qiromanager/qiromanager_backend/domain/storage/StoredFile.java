package com.qiromanager.qiromanager_backend.domain.storage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoredFile {
    private String url;
    private String publicId;
}