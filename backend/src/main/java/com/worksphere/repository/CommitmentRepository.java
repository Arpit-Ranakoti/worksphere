package com.worksphere.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksphere.entity.Commitment;
import com.worksphere.entity.CommitmentStatus;

public interface CommitmentRepository extends JpaRepository<Commitment, Long> {

    List<Commitment> findByWorkerIdOrderByIdDesc(Long workerId);

    List<Commitment> findByEmployerIdOrderByIdDesc(Long employerId);

    Optional<Commitment> findByIdAndWorkerId(Long id, Long workerId);

    Optional<Commitment> findByIdAndEmployerId(Long id, Long employerId);

    List<Commitment> findByJobIdAndStatusIn(Long jobId, Collection<CommitmentStatus> statuses);

    boolean existsByJobIdAndStatusIn(Long jobId, Collection<CommitmentStatus> statuses);
}
