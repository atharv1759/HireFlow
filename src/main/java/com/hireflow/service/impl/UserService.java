package com.hireflow.service.impl;

import com.hireflow.dto.request.ProfileUpdateRequest;
import com.hireflow.dto.response.ApiResponse;
import com.hireflow.entity.User;
import com.hireflow.exception.ResourceNotFoundException;
import com.hireflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    public ApiResponse.UserResponse getProfile() {
        return mapToResponse(getCurrentUser());
    }

    @Transactional
    public ApiResponse.UserResponse updateProfile(ProfileUpdateRequest request) {
        User user = getCurrentUser();

        if (request.getName() != null && !request.getName().isBlank()) user.setName(request.getName());
        if (request.getLocation() != null) user.setLocation(request.getLocation());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        if (user.getRole() == User.Role.JOBSEEKER) {
            if (request.getTitle() != null) user.setTitle(request.getTitle());
            if (request.getBio() != null) user.setBio(request.getBio());
            if (request.getSkills() != null) user.setSkills(request.getSkills());
        } else if (user.getRole() == User.Role.COMPANY) {
            if (request.getCompanyName() != null) user.setCompanyName(request.getCompanyName());
            if (request.getIndustry() != null) user.setIndustry(request.getIndustry());
            if (request.getCompanySize() != null) user.setCompanySize(request.getCompanySize());
            if (request.getWebsite() != null) user.setWebsite(request.getWebsite());
            if (request.getCompanyDescription() != null) user.setCompanyDescription(request.getCompanyDescription());
        }

        user = userRepository.save(user);
        log.info("Profile updated for user: {}", user.getEmail());
        return mapToResponse(user);
    }

    public ApiResponse.UserResponse mapToResponse(User user) {
        return ApiResponse.UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .title(user.getTitle())
                .bio(user.getBio())
                .location(user.getLocation())
                .phone(user.getPhone())
                .resumeUrl(user.getResumeUrl())
                .skills(user.getSkills())
                .companyName(user.getCompanyName())
                .industry(user.getIndustry())
                .companySize(user.getCompanySize())
                .website(user.getWebsite())
                .companyDescription(user.getCompanyDescription())
                .build();
    }
}
