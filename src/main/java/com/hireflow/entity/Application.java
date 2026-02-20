package com.hireflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "applications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "applicant_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    private String phone;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "company_notes", columnDefinition = "TEXT")
    private String companyNotes;

    public enum ApplicationStatus {
        PENDING, REVIEWED, SHORTLISTED, ACCEPTED, REJECTED
    }
}
