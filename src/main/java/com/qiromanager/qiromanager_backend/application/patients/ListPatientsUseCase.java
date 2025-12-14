package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.api.patients.TherapistSummary;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListPatientsUseCase {

    private final PatientRepository patientRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional(readOnly = true)
    public List<PatientResponse> execute() {

        User currentUser = authenticatedUserService.getCurrentUser();

        List<Patient> patients;

        if (authenticatedUserService.isAdmin(currentUser)) {
            patients = patientRepository.findAllActive();
        } else {
            patients = patientRepository.findAllActive()
                    .stream()
                    .filter(patient -> authenticatedUserService.isAssignedToPatient(currentUser, patient))
                    .toList();
        }

        return patients.stream()
                .map(patient -> {

                    List<TherapistSummary> therapistSummaries =
                            patient.getTherapists().stream()
                                    .map(t -> TherapistSummary.builder()
                                            .id(t.getId())
                                            .fullName(t.getFullName())
                                            .build())
                                    .toList();

                    return PatientResponse.builder()
                            .id(patient.getId())
                            .fullName(patient.getFullName())
                            .dateOfBirth(patient.getDateOfBirth())
                            .phone(patient.getPhone())
                            .email(patient.getEmail())
                            .address(patient.getAddress())
                            .generalNotes(patient.getGeneralNotes())
                            .active(patient.isActive())
                            .createdAt(patient.getCreatedAt())
                            .updatedAt(patient.getUpdatedAt())
                            .therapists(therapistSummaries)
                            .build();

                }).toList();
    }
}
