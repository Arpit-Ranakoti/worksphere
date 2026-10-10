package com.worksphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksphere.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findAllByOrderByCreatedAtDesc();

    List<Job> findByEmployerIdOrderByCreatedAtDesc(Long employerId);
}
