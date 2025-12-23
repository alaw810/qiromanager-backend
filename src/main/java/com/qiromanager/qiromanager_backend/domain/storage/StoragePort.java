package com.qiromanager.qiromanager_backend.domain.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StoragePort {
    StoredFile upload(MultipartFile file);
    void delete(String publicId);
}