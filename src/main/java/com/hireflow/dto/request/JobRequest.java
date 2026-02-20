package com.hireflow.dto.request;

import com.hireflow.entity.Job;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    private String requirements;

    @NotBlank(message = "Location is required")
    private String location;

    private String salary;

    @NotNull(message = "Job type is required")
    private Job.JobType type;

    @NotNull(message = "Job level is required")
    private Job.JobLevel level;

    @NotNull(message = "Job category is required")
    private Job.JobCategory category;

    private List<String> skills;
    private List<String> benefits;
    private LocalDateTime deadline;
    private Job.JobStatus status;
}
