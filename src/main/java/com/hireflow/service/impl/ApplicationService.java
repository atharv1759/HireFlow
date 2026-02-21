package com.hireflow.service.impl;

import com.hireflow.dto.request.ApplicationRequest;
import com.hireflow.dto.response.ApiResponse;
import com.hireflow.entity.Application;
import com.hireflow.entity.Job;
import com.hireflow.entity.User;
import com.hireflow.exception.AccessDeniedException;
import com.hireflow.exception.DuplicateResourceException;
import com.hireflow.exception.ResourceNotFoundException;
import com.hireflow.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobService jobService;
    private final UserService userService;

    @Transactional
    public ApiResponse.ApplicationResponse apply(Long jobId, ApplicationRequest request) {
        User applicant = userService.getCurrentUser();
        Job job = jobService.findJobOrThrow(jobId);

        if (job.getStatus() != Job.JobStatus.ACTIVE) {
            throw new IllegalArgumentException("This job is no longer accepting applications");
        }

        if (applicationRepository.existsByJobAndApplicant(job, applicant)) {
            throw new DuplicateResourceException("You have already applied to this job");
        }

        Application application = Application.builder()
                .job(job)
                .applicant(applicant)
                .coverLetter(request.getCoverLetter())
                .phone(request.getPhone())
                .portfolioUrl(request.getPortfolioUrl())
                .resumeUrl(request.getResumeUrl())
                .status(Application.ApplicationStatus.PENDING)
                .build();

        application = applicationRepository.save(application);
        log.info("Application submitted: userId={} for jobId={}", applicant.getId(), jobId);
        return mapToResponse(application);
    }

    @Transactional(readOnly = true)
    public List<ApiResponse.ApplicationResponse> getMyApplications() {
        User applicant = userService.getCurrentUser();
        return applicationRepository.findByApplicantOrderByCreatedAtDesc(applicant)
                .stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public boolean hasApplied(Long jobId) {
        User applicant = userService.getCurrentUser();
        Job job = jobService.findJobOrThrow(jobId);
        return applicationRepository.existsByJobAndApplicant(job, applicant);
    }

    @Transactional(readOnly = true)
    public List<ApiResponse.ApplicationResponse> getApplicationsForJob(Long jobId) {
        User company = userService.getCurrentUser();
        Job job = jobService.findJobOrThrow(jobId);

        if (!job.getCompany().getId().equals(company.getId())) {
            throw new AccessDeniedException("You can only view applications for your own jobs");
        }

        return applicationRepository.findByJobOrderByCreatedAtDesc(job)
                .stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public ApiResponse.ApplicationResponse updateStatus(Long applicationId,
                                                         Application.ApplicationStatus status,
                                                         String notes) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        User company = userService.getCurrentUser();
        if (!application.getJob().getCompany().getId().equals(company.getId())) {
            throw new AccessDeniedException("You can only update applications for your own jobs");
        }

        application.setStatus(status);
        if (notes != null) application.setCompanyNotes(notes);
        application = applicationRepository.save(application);

        log.info("Application {} status updated to {} by company {}",
                applicationId, status, company.getEmail());
        return mapToResponse(application);
    }

    public ApiResponse.ApplicationResponse mapToResponse(Application app) {
        return ApiResponse.ApplicationResponse.builder()
                .id(app.getId())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .companyName(app.getJob().getCompany().getCompanyName() != null
                        ? app.getJob().getCompany().getCompanyName()
                        : app.getJob().getCompany().getName())
                .applicantId(app.getApplicant().getId())
                .applicantName(app.getApplicant().getName())
                .applicantEmail(app.getApplicant().getEmail())
                .coverLetter(app.getCoverLetter())
                .phone(app.getPhone())
                .portfolioUrl(app.getPortfolioUrl())
                .status(app.getStatus().name())
                .companyNotes(app.getCompanyNotes())
                .createdAt(app.getCreatedAt())
                .build();
    }
}
