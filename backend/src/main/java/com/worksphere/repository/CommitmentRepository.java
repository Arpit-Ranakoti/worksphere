package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface CommitmentRepository extends JpaRepository<Commitment,Long> { List<Commitment> findByWorkerIdOrderByIdDesc(Long workerId); List<Commitment> findByEmployerIdOrderByIdDesc(Long employerId); Optional<Commitment> findByIdAndWorkerId(Long id,Long workerId); Optional<Commitment> findByIdAndEmployerId(Long id,Long employerId); }
