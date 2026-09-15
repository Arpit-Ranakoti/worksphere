package com.worksphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="notifications")
public class Notification { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public User user; @Enumerated(EnumType.STRING) public NotificationType type=NotificationType.SYSTEM; @Column(length=1000) public String message; public boolean readFlag=false; public LocalDateTime createdAt=LocalDateTime.now(); }
