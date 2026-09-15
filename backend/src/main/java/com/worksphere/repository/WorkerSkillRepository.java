package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface WorkerSkillRepository extends JpaRepository<WorkerSkill,Long> { List<WorkerSkill> findByWorkerId(Long workerId); void deleteByWorkerId(Long workerId); }
