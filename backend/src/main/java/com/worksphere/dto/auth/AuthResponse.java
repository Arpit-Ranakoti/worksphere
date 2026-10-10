package com.worksphere.dto.auth;

import com.worksphere.entity.User;

public record AuthResponse(String token, User user) {}
