package com.worksphere.entity;
import jakarta.persistence.*;
@Entity @Table(name="skills")
public class Skill { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(unique=true,nullable=false) public String name; public Skill(){} public Skill(String n){name=n;} }
