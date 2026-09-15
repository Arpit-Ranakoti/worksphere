package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SkillRepository extends JpaRepository<Skill,Long> { Optional<Skill> findByNameIgnoreCase(String name); List<Skill> findAllByOrderByNameAsc(); }
