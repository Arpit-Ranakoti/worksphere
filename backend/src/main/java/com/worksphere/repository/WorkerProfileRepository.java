package com.worksphere.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksphere.entity.WorkerProfile;

public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, Long> {

    Optional<WorkerProfile> findByUserId(Long userId);
}
