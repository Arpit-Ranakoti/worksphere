package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface JobSkillRepository extends JpaRepository<JobSkill,Long> { List<JobSkill> findByJobId(Long jobId); void deleteByJobId(Long jobId); }
