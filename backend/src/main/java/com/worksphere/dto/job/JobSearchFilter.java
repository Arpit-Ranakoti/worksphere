package com.worksphere.dto.job;

public record JobSearchFilter(String keyword, String location, String category, String date, Double minPay, Double maxPay, Boolean recommended) {}
