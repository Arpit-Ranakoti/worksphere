package com.worksphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksphere.entity.Commitment;

public interface CommitmentRepository extends JpaRepository<Commitment, Long> {

    List<Commitment> findByWorkerIdOrderByIdDesc(Long workerId);

    List<Commitment> findByEmployerIdOrderByIdDesc(Long employerId);

    Optional<Commitment> findByIdAndWorkerId(Long id, Long workerId);

    Optional<Commitment> findByIdAndEmployerId(Long id, Long employerId);
}
