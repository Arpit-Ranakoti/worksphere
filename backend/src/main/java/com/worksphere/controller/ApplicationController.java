package com.worksphere.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worksphere.dto.application.ApplicationRequest;
import com.worksphere.dto.application.ApplicationResponse;
import com.worksphere.service.ApplicationService;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService s;

    public ApplicationController(ApplicationService x) {
        s = x;
    }

    @PostMapping
    public ApplicationResponse apply(Authentication a, @RequestBody ApplicationRequest r) {
        return s.apply(a, r);
    }

    @GetMapping("/my")
    public Object mine(Authentication a) {
        return s.mine(a);
    }

    @PutMapping("/{id}/withdraw")
    public void withdraw(@PathVariable Long id, Authentication a) {
        s.withdraw(id, a);
    }

    @PutMapping("/{id}/select")
    public void select(@PathVariable Long id, Authentication a) {
        s.select(id, a);
    }

    @PutMapping("/{id}/reject")
    public void reject(@PathVariable Long id, Authentication a) {
        s.reject(id, a);
    }
}
