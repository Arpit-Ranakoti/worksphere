package com.worksphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="applications",uniqueConstraints=@UniqueConstraint(columnNames={"job_id","worker_id"}))
public class Application { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public Job job; @ManyToOne(optional=false) @JoinColumn(name="worker_id") public User worker; @Column(length=2000) public String coverMessage; @Enumerated(EnumType.STRING) public ApplicationStatus status=ApplicationStatus.PENDING; public LocalDateTime appliedAt=LocalDateTime.now(); }
