package com.worksphere.dto.notification;

import java.time.LocalDateTime;

public record NotificationResponse(Long id, String type, String message, Boolean read, LocalDateTime createdAt) {}
