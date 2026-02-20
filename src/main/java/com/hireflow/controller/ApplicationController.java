package com.hireflow.controller;

import com.hireflow.dto.request.ApplicationRequest;
import com.hireflow.dto.response.ApiResponse;
import com.hireflow.service.impl.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * POST /api/applications/jobs/:jobId
     * JobSeeker only: Apply to a job
     */
    @PostMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('JOBSEEKER')")
    public ResponseEntity<ApiResponse.ApplicationResponse> apply(
            @PathVariable Long jobId,
            @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.apply(jobId, request));
    }

    /**
     * GET /api/applications/my
     * JobSeeker only: Get all my applications
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('JOBSEEKER')")
    public ResponseEntity<List<ApiResponse.ApplicationResponse>> getMyApplications() {
        return ResponseEntity.ok(applicationService.getMyApplications());
    }

    /**
     * GET /api/applications/jobs/:jobId/check
     * JobSeeker only: Check if already applied
     */
    @GetMapping("/jobs/{jobId}/check")
    @PreAuthorize("hasRole('JOBSEEKER')")
    public ResponseEntity<Map<String, Boolean>> checkApplied(@PathVariable Long jobId) {
        boolean applied = applicationService.hasApplied(jobId);
        return ResponseEntity.ok(Map.of("applied", applied));
    }
}
