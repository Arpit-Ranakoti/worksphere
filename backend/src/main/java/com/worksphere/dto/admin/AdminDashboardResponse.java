package com.worksphere.dto.admin;

public record AdminDashboardResponse(long totalUsers, long totalWorkers, long totalEmployers, long openJobs, long completedJobs, long openDisputes) {}
