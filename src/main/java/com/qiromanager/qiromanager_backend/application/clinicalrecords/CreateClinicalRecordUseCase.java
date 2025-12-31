package com.qiromanager.qiromanager_backend.application.clinicalrecords;

import com.qiromanager.qiromanager_backend.api.clinicalrecords.ClinicalRecordResponse;
import com.qiromanager.qiromanager_backend.api.clinicalrecords.CreateClinicalRecordRequest;
import com.qiromanager.qiromanager_backend.api.mappers.ClinicalRecordMapper;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecord;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.ClinicalRecordRepository;
import com.qiromanager.qiromanager_backend.domain.exceptions.PatientNotFoundException;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.storage.StoragePort;
import com.qiromanager.qiromanager_backend.domain.storage.StoredFile;
import com.qiromanager.qiromanager_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CreateClinicalRecordUseCase {

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

        return mapper.toResponse(savedRecord);
    }
}