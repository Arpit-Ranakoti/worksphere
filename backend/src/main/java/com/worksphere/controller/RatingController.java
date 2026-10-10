package com.worksphere.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worksphere.dto.rating.RatingRequest;
import com.worksphere.service.RatingService;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService s;

    public RatingController(RatingService x) {
        s = x;
    }

    @PostMapping
    public Object rate(Authentication a, @RequestBody RatingRequest r) {
        return s.rate(a, r);
    }
}
