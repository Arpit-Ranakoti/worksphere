package com.worksphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="disputes")
public class Dispute { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public Commitment commitment; @ManyToOne(optional=false) public User raisedBy; public String reason; @Column(length=4000) public String description; @Enumerated(EnumType.STRING) public DisputeStatus status=DisputeStatus.OPEN_DISPUTE; @Column(length=4000) public String resolutionNote; public LocalDateTime createdAt=LocalDateTime.now(); public LocalDateTime resolvedAt; }
