package com.worksphere.dto.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ApplicationResponse(Long id, Long jobId, String jobTitle, String jobLocation, LocalDate jobDate, String workerName, Double workerRating, List<String> workerSkills, Double matchScore, String coverMessage, String status, LocalDateTime appliedAt) {}
