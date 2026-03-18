package com.qiromanager.qiromanager_backend.application.patients;

import com.qiromanager.qiromanager_backend.api.patients.CreatePatientRequest;
import com.qiromanager.qiromanager_backend.api.patients.PatientResponse;
import com.qiromanager.qiromanager_backend.application.audit.AuditService;
import com.qiromanager.qiromanager_backend.application.users.AuthenticatedUserService;
import com.qiromanager.qiromanager_backend.domain.patient.Patient;
import com.qiromanager.qiromanager_backend.domain.patient.PatientRepository;
import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePatientUseCaseTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private CreatePatientUseCase createPatientUseCase;

    @Test
    void execute_shouldCreatePatient_andAssignCurrentTherapist() {
        User therapist = User.create("Jane Doe", "janedoe", "jane@clinic.com", "pass", Role.USER);
        therapist.forceId(1L);

        CreatePatientRequest request = new CreatePatientRequest();
        request.setFullName("John Patient");
        request.setDateOfBirth(LocalDate.of(1985, 6, 15));
        request.setPhone("600123456");
        request.setEmail("john@patient.com");

        when(authenticatedUserService.getCurrentUser()).thenReturn(therapist);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.assignTherapist(therapist);
            return p;
        });

        PatientResponse response = createPatientUseCase.execute(request);

        assertThat(response).isNotNull();
        assertThat(response.getFullName()).isEqualTo("John Patient");
        assertThat(response.isActive()).isTrue();

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());
        Patient saved = captor.getValue();
        assertThat(saved.getTherapists()).contains(therapist);
    }

    @Test
    void execute_shouldCreatePatient_withMinimalData() {
        User therapist = User.create("Jane Doe", "janedoe", "jane@clinic.com", "pass", Role.USER);
        therapist.forceId(1L);

        CreatePatientRequest request = new CreatePatientRequest();
        request.setFullName("Minimal Patient");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(authenticatedUserService.getCurrentUser()).thenReturn(therapist);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponse response = createPatientUseCase.execute(request);

        assertThat(response.getFullName()).isEqualTo("Minimal Patient");
        verify(patientRepository).save(any(Patient.class));
    }
}
