package com.worksphere.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "strikes")
public class Strike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne(optional = false)
    public User user;
    @Enumerated(EnumType.STRING)
    public StrikeStatus status = StrikeStatus.ACTIVE;
    public String reason;
    public LocalDateTime createdAt = LocalDateTime.now();
}
