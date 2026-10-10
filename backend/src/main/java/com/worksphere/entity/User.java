package com.worksphere.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(nullable = false)
    public String fullName;
    @Column(nullable = false, unique = true)
    public String email;
    public String phone;
    @JsonIgnore
    @Column(nullable = false)
    public String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role role;
    public boolean active = true;
    public LocalDateTime createdAt = LocalDateTime.now();
}
