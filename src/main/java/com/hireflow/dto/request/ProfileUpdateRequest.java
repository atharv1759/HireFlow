package com.hireflow.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class ProfileUpdateRequest {
    private String name;
    private String location;
    private String phone;

    // JobSeeker fields
    private String title;
    private String bio;
    private List<String> skills;

    // Company fields
    private String companyName;
    private String industry;
    private String companySize;
    private String website;
    private String companyDescription;
}
