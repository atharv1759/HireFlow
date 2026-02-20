package com.hireflow.controller;

import com.hireflow.dto.response.ApiResponse;
import com.hireflow.entity.Application;
import com.hireflow.service.impl.ApplicationService;
import com.hireflow.service.impl.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/company")
@PreAuthorize("hasRole('COMPANY')")
@RequiredArgsConstructor
public class CompanyController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    /**
     * GET /api/company/dashboard
     * Get dashboard statistics for the logged-in company
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse.DashboardStats> getDashboard() {
        return ResponseEntity.ok(jobService.getDashboardStats());
    }

    /**
     * GET /api/company/jobs
     * Get all jobs posted by this company
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<ApiResponse.JobResponse>> getMyJobs() {
        return ResponseEntity.ok(jobService.getMyJobs());
    }

    /**
     * GET /api/company/jobs/:jobId/applications
     * Get all applications for a specific job
     */
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<ApiResponse.ApplicationResponse>> getJobApplications(
            @PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsForJob(jobId));
    }

    /**
     * PATCH /api/company/applications/:id/status
     * Update an application's status
     * Body: { "status": "SHORTLISTED", "notes": "Great candidate" }
     */
    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<ApiResponse.ApplicationResponse> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        Application.ApplicationStatus status =
                Application.ApplicationStatus.valueOf(body.get("status").toUpperCase());
        String notes = body.get("notes");

        return ResponseEntity.ok(applicationService.updateStatus(id, status, notes));
    }
}
