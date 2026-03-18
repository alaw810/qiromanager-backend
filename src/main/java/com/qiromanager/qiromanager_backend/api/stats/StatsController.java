package com.qiromanager.qiromanager_backend.api.stats;

import com.qiromanager.qiromanager_backend.application.stats.GetDashboardStatsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard Stats", description = "Aggregated statistics for the dashboard. Requires JWT.")
@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final GetDashboardStatsUseCase getDashboardStatsUseCase;

    @Operation(summary = "Get dashboard stats. Admins see global data; therapists see their own.")
    @ApiResponse(responseCode = "200", description = "Dashboard statistics")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(getDashboardStatsUseCase.execute());
    }
}