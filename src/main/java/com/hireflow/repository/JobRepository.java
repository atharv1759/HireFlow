package com.hireflow.repository;

import com.hireflow.entity.Job;
import com.hireflow.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByCompanyOrderByCreatedAtDesc(User company);

    Page<Job> findByStatus(Job.JobStatus status, Pageable pageable);

    @Query("""
        SELECT j FROM Job j
        WHERE j.status = 'ACTIVE'
          AND (:search IS NULL OR :search = '' OR
               LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:type IS NULL OR j.type = :type)
          AND (:level IS NULL OR j.level = :level)
          AND (:category IS NULL OR j.category = :category)
          AND (:location IS NULL OR :location = '' OR
               LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
        ORDER BY j.createdAt DESC
        """)
    Page<Job> findJobsWithFilters(
            @Param("search") String search,
            @Param("type") Job.JobType type,
            @Param("level") Job.JobLevel level,
            @Param("category") Job.JobCategory category,
            @Param("location") String location,
            Pageable pageable
    );

    long countByCompanyAndStatus(User company, Job.JobStatus status);
}
