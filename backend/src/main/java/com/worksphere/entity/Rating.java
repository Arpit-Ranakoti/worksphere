package com.worksphere.entity;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="ratings",uniqueConstraints=@UniqueConstraint(columnNames={"commitment_id","from_user_id","to_user_id"}))
public class Rating { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public Commitment commitment; @ManyToOne(optional=false) @JoinColumn(name="from_user_id") public User fromUser; @ManyToOne(optional=false) @JoinColumn(name="to_user_id") public User toUser; public Integer rating; @Column(length=2000) public String review; public LocalDateTime createdAt=LocalDateTime.now(); }
