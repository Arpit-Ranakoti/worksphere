package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ApplicationRepository extends JpaRepository<Application,Long> { long countByJobId(Long jobId); Optional<Application> findByJobIdAndWorkerId(Long jobId,Long workerId); List<Application> findByWorkerIdOrderByAppliedAtDesc(Long workerId); List<Application> findByJobIdOrderByAppliedAtAsc(Long jobId); }
