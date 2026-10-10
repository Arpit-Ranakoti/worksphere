package com.worksphere.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worksphere.dto.auth.AuthResponse;
import com.worksphere.dto.auth.LoginRequest;
import com.worksphere.dto.auth.RegisterRequest;
import com.worksphere.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService s;

    public AuthController(AuthService x) {
        s = x;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest r) {
        return s.register(r);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest r) {
        return s.login(r);
    }
}
