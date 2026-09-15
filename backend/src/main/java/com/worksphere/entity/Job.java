package com.worksphere.entity;
import jakarta.persistence.*; import java.time.*;
@Entity @Table(name="jobs")
public class Job {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public User employer;
 @Column(nullable=false) public String title; public String category; @Column(length=4000) public String description; public String location; public LocalDate jobDate; public LocalTime startTime; public LocalTime endTime;
 public Double pay; public Integer workersRequired=1; public Integer workersSelected=0; public boolean depositRequired; public Double depositAmount=0.0;
 @Enumerated(EnumType.STRING) public JobStatus status=JobStatus.OPEN; public LocalDateTime createdAt=LocalDateTime.now();
}
