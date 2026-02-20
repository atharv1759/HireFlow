package com.hireflow.dto.request;

import com.hireflow.entity.Application;
import lombok.Data;

@Data
public class ApplicationRequest {
    private String coverLetter;
    private String phone;
    private String portfolioUrl;
}

