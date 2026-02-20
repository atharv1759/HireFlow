package com.hireflow.dto.response;

import com.hireflow.entity.Application;
import com.hireflow.entity.Job;
import com.hireflow.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class ApiResponse {

    // ===== AUTH =====
    @Data @Builder
    public static class AuthResponse {
        private String token;
        private String refreshToken;
        private String type;
        private Long id;
        private String name;
        private String email;
        private String role;
        private Long expiresIn;
    }

    // ===== USER =====
    @Data @Builder
    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private String role;
        private Boolean isActive;
        private LocalDateTime createdAt;

        // JobSeeker
        private String title;
        private String bio;
        private String location;
        private String phone;
        private String resumeUrl;
        private List<String> skills;

        // Company
        private String companyName;
        private String industry;
        private String companySize;
        private String website;
        private String companyDescription;
    }

    // ===== JOB =====
    @Data @Builder
    public static class JobResponse {
        private Long id;
        private String title;
        private String description;
        private String requirements;
        private String location;
        private String salary;
        private String type;
        private String level;
        private String category;
        private List<String> skills;
        private List<String> benefits;
        private LocalDateTime deadline;
        private String status;
        private Long companyId;
        private String companyName;
        private int applicationCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // ===== APPLICATION =====
    @Data @Builder
    public static class ApplicationResponse {
        private Long id;
        private Long jobId;
        private String jobTitle;
        private String companyName;
        private Long applicantId;
        private String applicantName;
        private String applicantEmail;
        private String coverLetter;
        private String phone;
        private String portfolioUrl;
        private String status;
        private String companyNotes;
        private LocalDateTime createdAt;
    }

    // ===== PAGINATED =====
    @Data @Builder
    public static class PageResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }

    // ===== COMPANY DASHBOARD =====
    @Data @Builder
    public static class DashboardStats {
        private long totalJobs;
        private long activeJobs;
        private long closedJobs;
        private long totalApplications;
        private long newApplicationsThisWeek;
    }

    // ===== GENERIC MESSAGE =====
    @Data @Builder
    public static class MessageResponse {
        private String message;
        private boolean success;
        private Object data;
    }
}
