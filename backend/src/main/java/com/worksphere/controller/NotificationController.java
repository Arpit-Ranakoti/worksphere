package com.worksphere.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worksphere.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService s;

    public NotificationController(NotificationService x) {
        s = x;
    }

    @GetMapping
    public Object mine(Authentication a) {
        return s.mine(a);
    }

    @PutMapping("/{id}/read")
    public void read(@PathVariable Long id, Authentication a) {
        s.read(id, a);
    }
}
