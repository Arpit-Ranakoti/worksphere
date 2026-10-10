package com.worksphere.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worksphere.dto.dispute.DisputeRequest;
import com.worksphere.dto.dispute.DisputeResponse;
import com.worksphere.service.DisputeService;

@RestController
@RequestMapping("/api/disputes")
public class DisputeController {

    private final DisputeService s;

    public DisputeController(DisputeService x) {
        s = x;
    }

    @PostMapping
    public DisputeResponse create(Authentication a, @RequestBody DisputeRequest r) {
        return s.create(a, r);
    }

    @GetMapping("/my")
    public Object mine(Authentication a) {
        return s.mine(a);
    }
}
