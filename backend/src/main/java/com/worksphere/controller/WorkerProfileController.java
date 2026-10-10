package com.worksphere.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worksphere.dto.profile.WorkerProfileRequest;
import com.worksphere.dto.profile.WorkerProfileResponse;
import com.worksphere.service.WorkerProfileService;

@RestController
@RequestMapping("/api/users/me/worker-profile")
public class WorkerProfileController {

    private final WorkerProfileService s;

    public WorkerProfileController(WorkerProfileService x) {
        s = x;
    }

    @GetMapping
    public WorkerProfileResponse get(Authentication a) {
        return s.get(a);
    }

    @PutMapping
    public WorkerProfileResponse save(Authentication a, @RequestBody WorkerProfileRequest r) {
        return s.save(a, r);
    }
}
