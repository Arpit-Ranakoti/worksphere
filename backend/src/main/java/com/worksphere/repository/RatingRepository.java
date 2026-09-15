package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface RatingRepository extends JpaRepository<Rating,Long> { List<Rating> findByToUserIdOrderByCreatedAtDesc(Long userId); Optional<Rating> findByCommitmentIdAndFromUserIdAndToUserId(Long c,Long f,Long t); }
