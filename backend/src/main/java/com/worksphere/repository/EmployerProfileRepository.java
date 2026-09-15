package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface EmployerProfileRepository extends JpaRepository<EmployerProfile,Long> { Optional<EmployerProfile> findByUserId(Long userId); }
