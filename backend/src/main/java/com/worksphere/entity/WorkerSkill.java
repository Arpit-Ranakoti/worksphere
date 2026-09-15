package com.worksphere.entity;
import jakarta.persistence.*;
@Entity @Table(name="worker_skills",uniqueConstraints=@UniqueConstraint(columnNames={"worker_id","skill_id"}))
public class WorkerSkill { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) @JoinColumn(name="worker_id") public WorkerProfile worker; @ManyToOne(optional=false) @JoinColumn(name="skill_id") public Skill skill; }
