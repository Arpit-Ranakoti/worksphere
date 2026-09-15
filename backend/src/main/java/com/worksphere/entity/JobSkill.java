package com.worksphere.entity;
import jakarta.persistence.*;
@Entity @Table(name="job_skills",uniqueConstraints=@UniqueConstraint(columnNames={"job_id","skill_id"}))
public class JobSkill { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) @JoinColumn(name="job_id") public Job job; @ManyToOne(optional=false) @JoinColumn(name="skill_id") public Skill skill; }
