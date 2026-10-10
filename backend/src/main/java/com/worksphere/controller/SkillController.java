package com.worksphere.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worksphere.service.SkillService;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService s;

    public SkillController(SkillService x) {
        s = x;
    }

    @GetMapping
    public Object all() {
        return s.all();
    }
}
