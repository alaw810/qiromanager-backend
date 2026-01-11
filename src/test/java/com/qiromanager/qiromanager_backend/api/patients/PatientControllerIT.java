package com.qiromanager.qiromanager_backend.api.patients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiromanager.qiromanager_backend.application.patients.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private CreatePatientUseCase createPatientUseCase;
    @MockitoBean private ListPatientsUseCase listPatientsUseCase;
    @MockitoBean private GetPatientByIdUseCase getPatientByIdUseCase;
    @MockitoBean private UpdatePatientUseCase updatePatientUseCase;
    @MockitoBean private UpdatePatientStatusUseCase updatePatientStatusUseCase;
    @MockitoBean private SearchPatientsUseCase searchPatientsUseCase;
    @MockitoBean private AssignPatientUseCase assignPatientUseCase;
    @MockitoBean private UnassignPatientUseCase unassignPatientUseCase;

    @Test
    @WithMockUser(username = "therapist", roles = {"USER"})
    void createPatient_ShouldReturn201_WhenRequestIsValid() throws Exception {
        CreatePatientRequest request = new CreatePatientRequest();
        request.setFullName("New Patient");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setEmail("new@patient.com");

        PatientResponse response = PatientResponse.builder()
                .id(1L)
                .fullName("New Patient")
                .active(true)
                .build();

        when(createPatientUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("New Patient"));
    }

    @Test
    @WithMockUser(username = "therapist", roles = {"USER"})
    void getAllPatients_ShouldReturnList() throws Exception {
        PatientResponse p1 = PatientResponse.builder().id(1L).fullName("P1").build();
        when(listPatientsUseCase.execute(any())).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/v1/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("P1"));
    }

    @Test
    @WithMockUser(username = "therapist", roles = {"USER"})
    void createPatient_ShouldReturn400_WhenNameIsEmpty() throws Exception {
        CreatePatientRequest request = new CreatePatientRequest();

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "therapist", roles = {"USER"})
    void getPatientById_ShouldReturnPatient_WhenExists() throws Exception {
        PatientResponse response = PatientResponse.builder()
                .id(1L)
                .fullName("Existing Patient")
                .build();

        when(getPatientByIdUseCase.execute(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Existing Patient"));
    }

    @Test
    @WithMockUser(username = "therapist", roles = {"USER"})
    void updatePatient_ShouldReturnUpdatedData() throws Exception {
        UpdatePatientRequest request = new UpdatePatientRequest();
        request.setFullName("Updated Name");
        request.setEmail("updated@test.com");

        PatientResponse response = PatientResponse.builder()
                .id(1L)
                .fullName("Updated Name")
                .email("updated@test.com")
                .build();

        when(updatePatientUseCase.execute(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"));
    }

    @Test
    @WithMockUser(username = "therapist", roles = {"USER"})
    void searchPatients_ShouldReturnResults() throws Exception {
        PatientResponse p1 = PatientResponse.builder().id(1L).fullName("John Doe").build();

        when(searchPatientsUseCase.execute("John")).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/v1/patients/search")
                        .param("query", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("John Doe"));
    }

    @Test
    @WithMockUser(username = "therapist", roles = {"USER"})
    void assignPatient_ShouldReturn200() throws Exception {
        PatientResponse response = PatientResponse.builder().id(1L).build();

        when(assignPatientUseCase.execute(1L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/patients/1/assign"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "therapist", roles = {"USER"})
    void unassignPatient_ShouldReturn200() throws Exception {
        PatientResponse response = PatientResponse.builder().id(1L).build();

        when(unassignPatientUseCase.execute(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/patients/1/assign"))
                .andExpect(status().isOk());
    }
}