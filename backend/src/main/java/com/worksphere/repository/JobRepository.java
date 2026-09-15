package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface JobRepository extends JpaRepository<Job,Long> { List<Job> findAllByOrderByCreatedAtDesc(); List<Job> findByEmployerIdOrderByCreatedAtDesc(Long employerId); }
