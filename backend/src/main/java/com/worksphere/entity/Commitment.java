package com.worksphere.entity;
import jakarta.persistence.*; import java.time.*;
@Entity @Table(name="commitments")
public class Commitment { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public Job job; @ManyToOne(optional=false) public User worker; @ManyToOne(optional=false) public User employer; @OneToOne public Application application; @Enumerated(EnumType.STRING) public CommitmentStatus status=CommitmentStatus.PENDING_CONFIRMATION; @Enumerated(EnumType.STRING) public DepositStatus depositStatus=DepositStatus.NOT_REQUIRED; public String cancelReason; public LocalDateTime confirmedAt; public LocalDateTime completedAt; }
