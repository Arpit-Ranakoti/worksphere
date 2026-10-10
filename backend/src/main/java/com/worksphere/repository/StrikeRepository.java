package com.worksphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksphere.entity.Strike;
import com.worksphere.entity.StrikeStatus;

public interface StrikeRepository extends JpaRepository<Strike, Long> {

    List<Strike> findByUserIdAndStatus(Long userId, StrikeStatus status);

    List<Strike> findAllByOrderByCreatedAtDesc();
}
