package com.hireflow.config;

import com.hireflow.entity.Job;
import com.hireflow.entity.User;
import com.hireflow.repository.JobRepository;
import com.hireflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData() {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("Database already seeded. Skipping.");
                return;
            }

            log.info("Seeding demo data...");

            // === DEMO USERS ===
            User jobSeeker = User.builder()
                    .name("Alex Rivera")
                    .email("jobseeker@demo.com")
                    .password(passwordEncoder.encode("demo123"))
                    .role(User.Role.JOBSEEKER)
                    .title("Full Stack Developer")
                    .bio("Passionate developer with 3 years of experience in React and Node.js.")
                    .location("San Francisco, CA")
                    .phone("+1 (555) 000-0000")
                    .skills(List.of("React", "Node.js", "TypeScript", "PostgreSQL"))
                    .isActive(true)
                    .build();
            userRepository.save(jobSeeker);

            User company = User.builder()
                    .name("TechCorp HR")
                    .email("company@demo.com")
                    .password(passwordEncoder.encode("demo123"))
                    .role(User.Role.COMPANY)
                    .companyName("TechCorp Inc.")
                    .industry("Technology")
                    .companySize("100-500")
                    .website("https://techcorp.example.com")
                    .companyDescription("We build innovative software solutions for businesses worldwide.")
                    .location("New York, NY")
                    .isActive(true)
                    .build();
            userRepository.save(company);

            // === DEMO JOBS ===
            List<Job> jobs = List.of(
                Job.builder()
                    .title("Senior React Developer")
                    .description("We are looking for an experienced React developer to join our growing team.\n\nResponsibilities:\n- Build and maintain React applications\n- Collaborate with the design team\n- Code reviews and mentoring\n- Performance optimization")
                    .requirements("React, TypeScript, Redux, REST APIs, 4+ years experience")
                    .location("New York, NY")
                    .salary("$120,000 – $160,000")
                    .type(Job.JobType.FULL_TIME)
                    .level(Job.JobLevel.SENIOR)
                    .category(Job.JobCategory.ENGINEERING)
                    .skills(List.of("React", "TypeScript", "Redux", "Node.js", "GraphQL"))
                    .benefits(List.of("Health Insurance", "Remote Options", "401k", "Stock Options"))
                    .deadline(LocalDateTime.now().plusDays(30))
                    .status(Job.JobStatus.ACTIVE)
                    .company(company)
                    .build(),

                Job.builder()
                    .title("UX/UI Designer")
                    .description("Join our product design team to craft beautiful and intuitive user experiences.\n\nResponsibilities:\n- Create wireframes, prototypes, and high-fidelity designs\n- Conduct user research\n- Maintain design systems")
                    .requirements("Figma, User Research, Design Systems, 3+ years experience")
                    .location("Remote")
                    .salary("$90,000 – $120,000")
                    .type(Job.JobType.REMOTE)
                    .level(Job.JobLevel.MID_LEVEL)
                    .category(Job.JobCategory.DESIGN)
                    .skills(List.of("Figma", "Prototyping", "User Research", "Design Systems", "CSS"))
                    .benefits(List.of("Fully Remote", "Health Insurance", "Learning Budget", "Flexible Hours"))
                    .deadline(LocalDateTime.now().plusDays(25))
                    .status(Job.JobStatus.ACTIVE)
                    .company(company)
                    .build(),

                Job.builder()
                    .title("Backend Engineer (Node.js)")
                    .description("We need a talented backend engineer to build robust APIs and microservices.\n\nResponsibilities:\n- Design and build RESTful and GraphQL APIs\n- Optimize database queries\n- Implement security best practices")
                    .requirements("Node.js, PostgreSQL, Docker, AWS, 3+ years experience")
                    .location("San Francisco, CA")
                    .salary("$110,000 – $145,000")
                    .type(Job.JobType.FULL_TIME)
                    .level(Job.JobLevel.MID_LEVEL)
                    .category(Job.JobCategory.ENGINEERING)
                    .skills(List.of("Node.js", "PostgreSQL", "Docker", "AWS", "Redis"))
                    .benefits(List.of("Health Insurance", "Dental", "401k", "Gym Membership"))
                    .deadline(LocalDateTime.now().plusDays(21))
                    .status(Job.JobStatus.ACTIVE)
                    .company(company)
                    .build(),

                Job.builder()
                    .title("Data Scientist")
                    .description("Looking for a data scientist to help us make data-driven decisions and build ML models.")
                    .requirements("Python, Machine Learning, SQL, Statistics, 2+ years experience")
                    .location("Austin, TX")
                    .salary("$100,000 – $140,000")
                    .type(Job.JobType.FULL_TIME)
                    .level(Job.JobLevel.MID_LEVEL)
                    .category(Job.JobCategory.DATA)
                    .skills(List.of("Python", "ML", "SQL", "TensorFlow", "Pandas"))
                    .benefits(List.of("Health Insurance", "Remote Options", "401k"))
                    .deadline(LocalDateTime.now().plusDays(14))
                    .status(Job.JobStatus.ACTIVE)
                    .company(company)
                    .build()
            );

            jobRepository.saveAll(jobs);

            log.info("✅ Demo data seeded successfully!");
            log.info("   - Job Seeker: jobseeker@demo.com / demo123");
            log.info("   - Company:    company@demo.com / demo123");
            log.info("   - {} jobs created", jobs.size());
        };
    }
}
