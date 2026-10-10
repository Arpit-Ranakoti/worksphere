package com.worksphere.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksphere.entity.EmployerProfile;

public interface EmployerProfileRepository extends JpaRepository<EmployerProfile, Long> {

    Optional<EmployerProfile> findByUserId(Long userId);
}
