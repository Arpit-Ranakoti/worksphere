package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface UserRepository extends JpaRepository<User,Long> { Optional<User> findByEmail(String email); List<User> findAllByOrderByCreatedAtDesc(); }
