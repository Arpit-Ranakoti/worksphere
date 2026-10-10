package com.worksphere.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "applications", uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "worker_id"}))
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne(optional = false)
    public Job job;
    @ManyToOne(optional = false)
    @JoinColumn(name = "worker_id")
    public User worker;
    @Column(length = 2000)
    public String coverMessage;
    @Enumerated(EnumType.STRING)
    public ApplicationStatus status = ApplicationStatus.PENDING;
    public LocalDateTime appliedAt = LocalDateTime.now();
}
