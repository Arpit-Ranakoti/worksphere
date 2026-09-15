package com.worksphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="strikes")
public class Strike { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public User user; @Enumerated(EnumType.STRING) public StrikeStatus status=StrikeStatus.ACTIVE; public String reason; public LocalDateTime createdAt=LocalDateTime.now(); }
