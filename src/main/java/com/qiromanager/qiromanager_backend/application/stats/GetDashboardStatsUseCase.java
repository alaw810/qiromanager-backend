package com.qiromanager.qiromanager_backend.application.stats;

import com.qiromanager.qiromanager_backend.api.stats.DashboardStatsResponse;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSessionRepository;
import com.qiromanager.qiromanager_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class GetDashboardStatsUseCase {

    private final PatientRepository patientRepository;
    private final TreatmentSessionRepository treatmentSessionRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional(readOnly = true)
    public DashboardStatsResponse execute() {
        User currentUser = authenticatedUserService.getCurrentUser();

        LocalDateTime startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

        long sessionsThisMonth;

        if (authenticatedUserService.isAdmin(currentUser)) {
            sessionsThisMonth = treatmentSessionRepository.countSessionsBetween(startOfMonth, endOfMonth);
        } else {
            sessionsThisMonth = treatmentSessionRepository.countTherapistSessionsBetween(currentUser.getId(), startOfMonth, endOfMonth);
        }

        long totalPatients = patientRepository.countAll();
        long activePatients = patientRepository.countActive();
        long myAssignedPatients = patientRepository.countByTherapistId(currentUser.getId());

        return DashboardStatsResponse.builder()
                .totalPatients(totalPatients)
                .activePatients(activePatients)
                .myAssignedPatients(myAssignedPatients)
                .sessionsThisMonth(sessionsThisMonth)
                .build();
    }
}