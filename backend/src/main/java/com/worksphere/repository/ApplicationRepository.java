package com.worksphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksphere.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    long countByJobId(Long jobId);

    Optional<Application> findByJobIdAndWorkerId(Long jobId, Long workerId);

    List<Application> findByWorkerIdOrderByAppliedAtDesc(Long workerId);

    List<Application> findByJobIdOrderByAppliedAtAsc(Long jobId);
}
