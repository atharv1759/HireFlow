package com.hireflow.repository;

import com.hireflow.entity.Application;
import com.hireflow.entity.Job;
import com.hireflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByApplicantOrderByCreatedAtDesc(User applicant);

    List<Application> findByJobOrderByCreatedAtDesc(Job job);

    Optional<Application> findByJobAndApplicant(Job job, User applicant);

    boolean existsByJobAndApplicant(Job job, User applicant);

    long countByApplicant(User applicant);

    @Query("""
        SELECT COUNT(a) FROM Application a
        WHERE a.job.company = :company
        AND a.createdAt >= :since
        """)
    long countNewApplicationsForCompany(
            @Param("company") User company,
            @Param("since") java.time.LocalDateTime since
    );

    @Query("""
        SELECT COUNT(a) FROM Application a
        WHERE a.job.company = :company
        """)
    long countTotalApplicationsForCompany(@Param("company") User company);
}
