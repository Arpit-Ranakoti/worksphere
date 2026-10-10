package com.worksphere.entity;

import java.time.LocalDateTime;

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
@Table(name = "disputes")
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne(optional = false)
    public Commitment commitment;
    @ManyToOne(optional = false)
    public User raisedBy;
    public String reason;
    @Column(length = 4000)
    public String description;
    @Enumerated(EnumType.STRING)
    public DisputeStatus status = DisputeStatus.OPEN_DISPUTE;
    @Column(length = 4000)
    public String resolutionNote;
    public LocalDateTime createdAt = LocalDateTime.now();
    public LocalDateTime resolvedAt;
}
