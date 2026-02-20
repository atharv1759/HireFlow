package com.hireflow.controller;

import com.hireflow.dto.request.JobRequest;
import com.hireflow.dto.response.ApiResponse;
import com.hireflow.service.impl.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /**
     * GET /api/jobs
     * Public: Browse all active jobs with filters & pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse.PageResponse<ApiResponse.JobResponse>> getJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                jobService.getJobs(search, type, level, category, location, page, size)
        );
    }

    /**
     * GET /api/jobs/:id
     * Public: Get job detail by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse.JobResponse> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    /**
     * POST /api/jobs
     * Company only: Create a new job listing
     */
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse.JobResponse> createJob(
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.createJob(request));
    }

    /**
     * PUT /api/jobs/:id
     * Company only: Update own job listing
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse.JobResponse> updateJob(
            @PathVariable Long id,
            @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.updateJob(id, request));
    }

    /**
     * DELETE /api/jobs/:id
     * Company only: Delete own job listing
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse.MessageResponse> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(
                ApiResponse.MessageResponse.builder()
                        .success(true)
                        .message("Job deleted successfully")
                        .build()
        );
    }
}
