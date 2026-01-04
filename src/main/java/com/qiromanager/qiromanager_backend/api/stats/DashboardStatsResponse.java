package com.qiromanager.qiromanager_backend.api.stats;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {
    private long totalPatients;
    private long activePatients;
    private long myAssignedPatients;
    private long sessionsThisMonth;
}