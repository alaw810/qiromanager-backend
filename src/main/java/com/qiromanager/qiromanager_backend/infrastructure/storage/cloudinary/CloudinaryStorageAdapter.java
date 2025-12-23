package com.qiromanager.qiromanager_backend.infrastructure.storage.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.qiromanager.qiromanager_backend.domain.storage.StoragePort;
import com.qiromanager.qiromanager_backend.domain.storage.StoredFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryStorageAdapter implements StoragePort {

    private final Cloudinary cloudinary;

    @Override
    public StoredFile upload(MultipartFile file) {
        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "resource_type", "auto"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            log.info("File uploaded successfully to Cloudinary. PublicID: {}", publicId);

            return StoredFile.builder()
                    .url(url)
                    .publicId(publicId)
                    .build();

        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary", e);
            throw new RuntimeException("Could not upload file to storage provider", e);
        }
    }

    @Override
    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("File deleted from Cloudinary: {}", publicId);

        } catch (Exception e) {
            log.warn("Could not delete file from Cloudinary: {}", publicId, e);
        }
    }
}