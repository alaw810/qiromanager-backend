package com.qiromanager.qiromanager_backend.api.clinicalrecords;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiromanager.qiromanager_backend.application.clinicalrecords.CreateClinicalRecordUseCase;
import com.qiromanager.qiromanager_backend.domain.clinicalhistory.RecordType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClinicalRecordControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateClinicalRecordUseCase createClinicalRecordUseCase;

    @Test
    @WithMockUser(username = "therapist", roles = {"USER"})
    void createClinicalRecord_ShouldReturn201_WhenRequestIsValid() throws Exception {
        CreateClinicalRecordRequest requestDto = new CreateClinicalRecordRequest();
        requestDto.setType(RecordType.EVOLUTION);
        requestDto.setContent("Patient progress is good.");

        ClinicalRecordResponse responseDto = ClinicalRecordResponse.builder()
                .id(1L)
                .content("Patient progress is good.")
                .type(RecordType.EVOLUTION)
                .build();

        when(createClinicalRecordUseCase.execute(eq(1L), any(), any()))
                .thenReturn(responseDto);

        MockMultipartFile filePart = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "PDF content".getBytes()
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/v1/patients/1/clinical-records")
                        .file(filePart)
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Patient progress is good."));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createClinicalRecord_ShouldWorkWithoutFile_WhenFileIsOptional() throws Exception {
        CreateClinicalRecordRequest requestDto = new CreateClinicalRecordRequest();
        requestDto.setType(RecordType.ANAMNESIS);
        requestDto.setContent("Initial interview.");

        ClinicalRecordResponse responseDto = ClinicalRecordResponse.builder()
                .id(2L)
                .content("Initial interview.")
                .build();

        when(createClinicalRecordUseCase.execute(eq(1L), any(), eq(null)))
                .thenReturn(responseDto);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request",
                "",
                "application/json",
                objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/v1/patients/1/clinical-records")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    void createClinicalRecord_ShouldReturn401_WhenUserNotAuthenticated() throws Exception {
        mockMvc.perform(multipart("/api/v1/patients/1/clinical-records"))
                .andExpect(status().isUnauthorized());
    }
}