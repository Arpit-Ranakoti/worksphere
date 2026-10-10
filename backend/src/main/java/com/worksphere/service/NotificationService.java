package com.worksphere.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.worksphere.dto.notification.NotificationResponse;
import com.worksphere.entity.Notification;
import com.worksphere.entity.NotificationType;
import com.worksphere.entity.User;
import com.worksphere.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository repo;
    private final UserService users;

    public NotificationService(NotificationRepository r, UserService u) {
        repo = r;
        users = u;
    }

    public void send(User u, NotificationType t, String m) {
        Notification n = new Notification();
        n.user = u;
        n.type = t;
        n.message = m;
        repo.save(n);
    }

    public List<NotificationResponse> mine(Authentication a) {
        return repo.findByUserIdOrderByCreatedAtDesc(users.current(a).id).stream().map(n -> new NotificationResponse(n.id, n.type.name(), n.message, n.readFlag, n.createdAt)).toList();
    }

    public void read(Long id, Authentication a) {
        Notification n = repo.findById(id).orElseThrow();
        if (!n.user.id.equals(users.current(a).id)) {
            throw new RuntimeException("Not your notification");
        
        }n.readFlag = true;
        repo.save(n);
    }
}
