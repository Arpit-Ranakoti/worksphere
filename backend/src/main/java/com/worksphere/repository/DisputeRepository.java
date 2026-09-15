package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface DisputeRepository extends JpaRepository<Dispute,Long> { List<Dispute> findByRaisedByIdOrderByCreatedAtDesc(Long userId); List<Dispute> findAllByOrderByCreatedAtDesc(); }
