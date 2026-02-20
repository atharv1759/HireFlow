package com.hireflow.service.impl;

import com.hireflow.dto.request.JobRequest;
import com.hireflow.dto.response.ApiResponse;
import com.hireflow.entity.Job;
import com.hireflow.entity.User;
import com.hireflow.exception.AccessDeniedException;
import com.hireflow.exception.ResourceNotFoundException;
import com.hireflow.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public ApiResponse.PageResponse<ApiResponse.JobResponse> getJobs(
            String search, String type, String level,
            String category, String location, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Job.JobType jobType = parseEnum(Job.JobType.class, type);
        Job.JobLevel jobLevel = parseEnum(Job.JobLevel.class, level);
        Job.JobCategory jobCategory = parseEnum(Job.JobCategory.class, category);

        Page<Job> jobPage = jobRepository.findJobsWithFilters(
                search, jobType, jobLevel, jobCategory, location, pageable);

        List<ApiResponse.JobResponse> content = jobPage.getContent()
                .stream().map(this::mapToResponse).toList();

        return ApiResponse.PageResponse.<ApiResponse.JobResponse>builder()
                .content(content)
                .page(jobPage.getNumber())
                .size(jobPage.getSize())
                .totalElements(jobPage.getTotalElements())
                .totalPages(jobPage.getTotalPages())
                .last(jobPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ApiResponse.JobResponse getJobById(Long id) {
        Job job = findJobOrThrow(id);
        return mapToResponse(job);
    }

    @Transactional
    public ApiResponse.JobResponse createJob(JobRequest request) {
        User company = userService.getCurrentUser();

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .location(request.getLocation())
                .salary(request.getSalary())
                .type(request.getType())
                .level(request.getLevel())
                .category(request.getCategory())
                .skills(request.getSkills())
                .benefits(request.getBenefits())
                .deadline(request.getDeadline() != null ? request.getDeadline()
                        : LocalDateTime.now().plusDays(30))
                .status(request.getStatus() != null ? request.getStatus() : Job.JobStatus.ACTIVE)
                .company(company)
                .build();

        job = jobRepository.save(job);
        log.info("Job created: '{}' by {}", job.getTitle(), company.getEmail());
        return mapToResponse(job);
    }

    @Transactional
    public ApiResponse.JobResponse updateJob(Long id, JobRequest request) {
        Job job = findJobOrThrow(id);
        User currentUser = userService.getCurrentUser();

        if (!job.getCompany().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only edit your own job listings");
        }

        if (request.getTitle() != null) job.setTitle(request.getTitle());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getRequirements() != null) job.setRequirements(request.getRequirements());
        if (request.getLocation() != null) job.setLocation(request.getLocation());
        if (request.getSalary() != null) job.setSalary(request.getSalary());
        if (request.getType() != null) job.setType(request.getType());
        if (request.getLevel() != null) job.setLevel(request.getLevel());
        if (request.getCategory() != null) job.setCategory(request.getCategory());
        if (request.getSkills() != null) job.setSkills(request.getSkills());
        if (request.getBenefits() != null) job.setBenefits(request.getBenefits());
        if (request.getDeadline() != null) job.setDeadline(request.getDeadline());
        if (request.getStatus() != null) job.setStatus(request.getStatus());

        job = jobRepository.save(job);
        log.info("Job updated: '{}' by {}", job.getTitle(), currentUser.getEmail());
        return mapToResponse(job);
    }

    @Transactional
    public void deleteJob(Long id) {
        Job job = findJobOrThrow(id);
        User currentUser = userService.getCurrentUser();

        if (!job.getCompany().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only delete your own job listings");
        }

        jobRepository.delete(job);
        log.info("Job deleted: id={} by {}", id, currentUser.getEmail());
    }

    @Transactional(readOnly = true)
    public List<ApiResponse.JobResponse> getMyJobs() {
        User company = userService.getCurrentUser();
        return jobRepository.findByCompanyOrderByCreatedAtDesc(company)
                .stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public ApiResponse.DashboardStats getDashboardStats() {
        User company = userService.getCurrentUser();
        List<Job> jobs = jobRepository.findByCompanyOrderByCreatedAtDesc(company);

        long totalJobs = jobs.size();
        long activeJobs = jobs.stream().filter(j -> j.getStatus() == Job.JobStatus.ACTIVE).count();
        long closedJobs = jobs.stream().filter(j -> j.getStatus() == Job.JobStatus.CLOSED).count();
        long totalApps = jobs.stream().mapToLong(j -> j.getApplications() != null ? j.getApplications().size() : 0).sum();
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long newApps = jobs.stream()
                .flatMap(j -> j.getApplications() != null ? j.getApplications().stream() : java.util.stream.Stream.empty())
                .filter(a -> a.getCreatedAt() != null && a.getCreatedAt().isAfter(weekAgo))
                .count();

        return ApiResponse.DashboardStats.builder()
                .totalJobs(totalJobs)
                .activeJobs(activeJobs)
                .closedJobs(closedJobs)
                .totalApplications(totalApps)
                .newApplicationsThisWeek(newApps)
                .build();
    }

    // ===== HELPERS =====
    public Job findJobOrThrow(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", id));
    }

    public ApiResponse.JobResponse mapToResponse(Job job) {
        return ApiResponse.JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .location(job.getLocation())
                .salary(job.getSalary())
                .type(job.getType().name())
                .level(job.getLevel().name())
                .category(job.getCategory().name())
                .skills(job.getSkills())
                .benefits(job.getBenefits())
                .deadline(job.getDeadline())
                .status(job.getStatus().name())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getCompanyName() != null
                        ? job.getCompany().getCompanyName() : job.getCompany().getName())
                .applicationCount(job.getApplications() != null ? job.getApplications().size() : 0)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    private <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumClass, value.toUpperCase().replace("-", "_").replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
