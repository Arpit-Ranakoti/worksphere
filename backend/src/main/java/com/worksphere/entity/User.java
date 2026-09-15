package com.worksphere.entity;
import jakarta.persistence.*; import com.fasterxml.jackson.annotation.JsonIgnore; import java.time.LocalDateTime;
@Entity @Table(name="users")
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String fullName;
 @Column(nullable=false,unique=true) public String email;
 public String phone;
 @JsonIgnore @Column(nullable=false) public String password;
 @Enumerated(EnumType.STRING) @Column(nullable=false) public Role role;
 public boolean active=true;
 public LocalDateTime createdAt=LocalDateTime.now();
}
