package com.worksphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.worksphere.entity.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByToUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Rating> findByCommitmentIdAndFromUserIdAndToUserId(Long c, Long f, Long t);
}
