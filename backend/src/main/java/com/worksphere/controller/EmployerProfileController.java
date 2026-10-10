package com.worksphere.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worksphere.dto.profile.EmployerProfileRequest;
import com.worksphere.dto.profile.EmployerProfileResponse;
import com.worksphere.service.EmployerProfileService;

@RestController
@RequestMapping("/api/users/me/employer-profile")
public class EmployerProfileController {

    private final EmployerProfileService s;

    public EmployerProfileController(EmployerProfileService x) {
        s = x;
    }

    @GetMapping
    public EmployerProfileResponse get(Authentication a) {
        return s.get(a);
    }

    @PutMapping
    public EmployerProfileResponse save(Authentication a, @RequestBody EmployerProfileRequest r) {
        return s.save(a, r);
    }
}
