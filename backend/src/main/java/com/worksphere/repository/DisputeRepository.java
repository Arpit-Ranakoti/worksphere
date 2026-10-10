package com.worksphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksphere.entity.Dispute;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {

    List<Dispute> findByRaisedByIdOrderByCreatedAtDesc(Long userId);

    List<Dispute> findAllByOrderByCreatedAtDesc();
}
