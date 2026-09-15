package com.worksphere.entity;
import jakarta.persistence.*;
@Entity @Table(name="worker_profiles")
public class WorkerProfile {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @OneToOne(optional=false) public User user;
 public String location; public String bio; public Double rating=0.0; public Integer ratingCount=0; public Integer strikeCount=0; public String availability;
}
