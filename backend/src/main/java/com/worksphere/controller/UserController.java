package com.worksphere.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worksphere.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService s;

    public UserController(UserService x) {
        s = x;
    }

    @GetMapping("/me")
    public Object me(Authentication a) {
        return s.current(a);
    }

    @GetMapping("/{id}/ratings")
    public Object ratings(@PathVariable Long id, com.worksphere.service.RatingService r) {
        return r.forUser(id);
    }
}
