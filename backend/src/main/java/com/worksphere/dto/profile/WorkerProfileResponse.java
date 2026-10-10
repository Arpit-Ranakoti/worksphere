package com.worksphere.dto.profile;

import java.util.List;

public record WorkerProfileResponse(Long id, Long userId, String fullName, String location, String bio, String availability, Double rating, Integer ratingCount, Integer strikeCount, List<String> skills) {}
