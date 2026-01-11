package com.qiromanager.qiromanager_backend.api.stats;

import com.qiromanager.qiromanager_backend.application.stats.GetDashboardStatsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StatsControllerIT {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private GetDashboardStatsUseCase getDashboardStatsUseCase;

    @Test
    @WithMockUser(roles = "USER")
    void getStats_ShouldReturnData() throws Exception {
        DashboardStatsResponse stats = DashboardStatsResponse.builder()
                .totalPatients(10)
                .activePatients(5)
                .myAssignedPatients(2)
                .sessionsThisMonth(3)
                .build();

        when(getDashboardStatsUseCase.execute()).thenReturn(stats);

        mockMvc.perform(get("/api/v1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPatients").value(10));
    }
}