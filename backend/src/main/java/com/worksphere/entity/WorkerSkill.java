package com.worksphere.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "worker_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"worker_id", "skill_id"}))
public class WorkerSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "worker_id")
    public WorkerProfile worker;
    @ManyToOne(optional = false)
    @JoinColumn(name = "skill_id")
    public Skill skill;
}
