package com.worksphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksphere.entity.JobSkill;

public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {

    List<JobSkill> findByJobId(Long jobId);

    void deleteByJobId(Long jobId);
}
