package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface StrikeRepository extends JpaRepository<Strike,Long> { List<Strike> findByUserIdAndStatus(Long userId,StrikeStatus status); List<Strike> findAllByOrderByCreatedAtDesc(); }
