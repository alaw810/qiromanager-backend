package com.qiromanager.qiromanager_backend.api.treatments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiromanager.qiromanager_backend.application.treatments.CreateTreatmentSessionUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TreatmentSessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateTreatmentSessionUseCase createTreatmentSessionUseCase;

    @Test
    @WithMockUser(username = "therapist", roles = {"USER"})
    void createSession_ShouldReturn201_WhenRequestIsValid() throws Exception {
        CreateTreatmentSessionRequest request = new CreateTreatmentSessionRequest();
        request.setSessionDate(LocalDateTime.now().minusHours(1));
        request.setNotes("Regular session.");

        TreatmentSessionResponse response = TreatmentSessionResponse.builder()
                .id(1L)
                .patientId(10L)
                .therapistName("Therapist John")
                .sessionDate(request.getSessionDate())
                .notes(request.getNotes())
                .build();

        when(createTreatmentSessionUseCase.execute(eq(10L), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/patients/10/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.therapistName").value("Therapist John"));
    }

    @Test
    void createSession_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/patients/10/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}