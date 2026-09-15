package com.worksphere.repository;
import com.worksphere.entity.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface NotificationRepository extends JpaRepository<Notification,Long> { List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId); }
