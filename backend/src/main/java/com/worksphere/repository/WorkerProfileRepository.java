package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile,Long> { Optional<WorkerProfile> findByUserId(Long userId); }
