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
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne(optional = false)
    public User user;
    @Enumerated(EnumType.STRING)
    public NotificationType type = NotificationType.SYSTEM;
    @Column(length = 1000)
    public String message;
    public boolean readFlag = false;
    public LocalDateTime createdAt = LocalDateTime.now();
}
