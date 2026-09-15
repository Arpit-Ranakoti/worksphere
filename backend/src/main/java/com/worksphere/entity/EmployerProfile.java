package com.worksphere.entity;
import jakarta.persistence.*;
@Entity @Table(name="employer_profiles")
public class EmployerProfile {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @OneToOne(optional=false) public User user;
 public String companyName; public String location; public String bio; public Double rating=0.0; public Integer ratingCount=0;
}
