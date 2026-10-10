package com.worksphere.dto.commitment;

import java.time.LocalDate;

public record CommitmentResponse(Long id, Long jobId, String jobTitle, String jobLocation, LocalDate jobDate, String startTime, String endTime, String workerName, String employerName, String status, String depositStatus, String cancelReason, Double pay, String otherPartyName, Long otherPartyUserId, Boolean ratedByMe) {}
