package com.worksphere.dto.auth;

import com.worksphere.entity.Role;

public record RegisterRequest(String fullName, String email, String phone, String password, Role role) {}
