package com.qiromanager.qiromanager_backend.application.clinicalrecords;

import com.qiromanager.qiromanager_backend.api.clinicalrecords.ClinicalRecordResponse;
import com.qiromanager.qiromanager_backend.api.clinicalrecords.CreateClinicalRecordRequest;
import com.qiromanager.qiromanager_backend.api.mappers.ClinicalRecordMapper;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.domain.exceptions.InvalidFileException;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.storage.StoragePort;
import com.qiromanager.qiromanager_backend.domain.storage.StoredFile;
import com.qiromanager.qiromanager_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateClinicalRecordUseCase {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );
    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10 MB

    private final ClinicalRecordRepository clinicalRecordRepository;
    private final PatientRepository patientRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final StoragePort storagePort;
    private final ClinicalRecordMapper mapper;

    @Transactional
    public ClinicalRecordResponse execute(Long patientId, CreateClinicalRecordRequest request, MultipartFile file) {
        User currentUser = authenticatedUserService.getCurrentUser();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        ClinicalRecord record = ClinicalRecord.create(
                patient,
                currentUser,
                request.getType(),
                request.getContent()
        );

        if (file != null && !file.isEmpty()) {
            validateFile(file);
            log.info("Uploading attachment for Clinical Record (Patient ID: {})", patientId);
            StoredFile storedFile = storagePort.upload(file);

            record.addAttachment(
                    storedFile.getUrl(),
                    storedFile.getPublicId(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize()
            );
        }

        ClinicalRecord savedRecord = clinicalRecordRepository.save(record);

        log.info("Clinical Record created successfully (ID: {})", savedRecord.getId());

        return mapper.toResponse(savedRecord);
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidFileException("File size exceeds the maximum allowed limit of 10 MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new InvalidFileException(
                    "File type not allowed. Accepted types: JPEG, PNG, PDF"
            );
        }
    }
}