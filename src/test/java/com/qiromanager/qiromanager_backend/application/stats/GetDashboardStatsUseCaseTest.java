package com.qiromanager.qiromanager_backend.application.stats;

import com.qiromanager.qiromanager_backend.api.stats.DashboardStatsResponse;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.treatment.TreatmentSessionRepository;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetDashboardStatsUseCaseTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private TreatmentSessionRepository treatmentSessionRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private GetDashboardStatsUseCase getDashboardStatsUseCase;

    @BeforeEach
    void setUp() {
        when(patientRepository.countAll()).thenReturn(50L);
        when(patientRepository.countActive()).thenReturn(40L);
    }

    @Test
    void execute_shouldReturnGlobalSessionCount_whenUserIsAdmin() {
        User admin = User.create("Admin", "admin", "admin@clinic.com", "pass", Role.ADMIN);
        admin.forceId(1L);

        when(authenticatedUserService.getCurrentUser()).thenReturn(admin);
        when(authenticatedUserService.isAdmin(admin)).thenReturn(true);
        when(patientRepository.countByTherapistId(1L)).thenReturn(10L);
        when(treatmentSessionRepository.countSessionsBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(25L);

        DashboardStatsResponse response = getDashboardStatsUseCase.execute();

        assertThat(response.getTotalPatients()).isEqualTo(50L);
        assertThat(response.getActivePatients()).isEqualTo(40L);
        assertThat(response.getSessionsThisMonth()).isEqualTo(25L);

        verify(treatmentSessionRepository).countSessionsBetween(any(), any());
        verify(treatmentSessionRepository, never()).countTherapistSessionsBetween(any(), any(), any());
    }

    @Test
    void execute_shouldReturnTherapistSessionCount_whenUserIsNotAdmin() {
        User therapist = User.create("Jane", "janedoe", "jane@clinic.com", "pass", Role.USER);
        therapist.forceId(2L);

        when(authenticatedUserService.getCurrentUser()).thenReturn(therapist);
        when(authenticatedUserService.isAdmin(therapist)).thenReturn(false);
        when(patientRepository.countByTherapistId(2L)).thenReturn(8L);
        when(treatmentSessionRepository.countTherapistSessionsBetween(eq(2L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(5L);

        DashboardStatsResponse response = getDashboardStatsUseCase.execute();

        assertThat(response.getSessionsThisMonth()).isEqualTo(5L);
        assertThat(response.getMyAssignedPatients()).isEqualTo(8L);

        verify(treatmentSessionRepository).countTherapistSessionsBetween(eq(2L), any(), any());
        verify(treatmentSessionRepository, never()).countSessionsBetween(any(), any());
    }
}
