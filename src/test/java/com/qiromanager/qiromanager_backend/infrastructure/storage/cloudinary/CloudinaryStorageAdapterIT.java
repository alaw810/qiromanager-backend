package com.qiromanager.qiromanager_backend.infrastructure.storage.cloudinary;

import com.qiromanager.qiromanager_backend.domain.storage.StoragePort;
import com.qiromanager.qiromanager_backend.domain.storage.StoredFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "CLOUDINARY_CLOUD_NAME", matches = ".*")
class CloudinaryStorageAdapterIT {

    @Autowired
    private StoragePort storagePort;

    @Test
    void shouldUploadAndDeleteFile_OnRealCloudinary() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.txt",
                "text/plain",
                "Hello Cloudinary! This is a test from Qiromanager.".getBytes()
        );

        System.out.println("Uploading to Cloudinary...");
        StoredFile storedFile = storagePort.upload(file);

        System.out.println("File uploaded. URL: " + storedFile.getUrl());
        System.out.println("Public ID: " + storedFile.getPublicId());

        assertThat(storedFile).isNotNull();
        assertThat(storedFile.getUrl()).startsWith("http");
        assertThat(storedFile.getUrl()).contains("cloudinary");
        assertThat(storedFile.getPublicId()).isNotBlank();

        System.out.println("Deleting file...");
        storagePort.delete(storedFile.getPublicId());
        System.out.println("Deleting complete.");
    }
}