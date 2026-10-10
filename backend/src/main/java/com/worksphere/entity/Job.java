package com.worksphere.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne(optional = false)
    public User employer;
    @Column(nullable = false)
    public String title;
    public String category;
    @Column(length = 4000)
    public String description;
    public String location;
    public LocalDate jobDate;
    public LocalTime startTime;
    public LocalTime endTime;
    public Double pay;
    public Integer workersRequired = 1;
    public Integer workersSelected = 0;
    public boolean depositRequired;
    public Double depositAmount = 0.0;
    @Enumerated(EnumType.STRING)
    public JobStatus status = JobStatus.OPEN;
    public LocalDateTime createdAt = LocalDateTime.now();
}
