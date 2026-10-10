package com.worksphere.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.worksphere.dto.profile.EmployerProfileRequest;
import com.worksphere.dto.profile.EmployerProfileResponse;
import com.worksphere.entity.EmployerProfile;
import com.worksphere.repository.EmployerProfileRepository;

@Service
public class EmployerProfileService {

    private final EmployerProfileRepository repo;
    private final UserService users;

    public EmployerProfileService(EmployerProfileRepository r, UserService u) {
        repo = r;
        users = u;
    }

    private EmployerProfile p(Authentication a) {
        return repo.findByUserId(users.current(a).id).orElseThrow();
    }

    public EmployerProfileResponse get(Authentication a) {
        EmployerProfile p = p(a);
        return new EmployerProfileResponse(p.id, p.user.id, p.user.fullName, p.companyName, p.location, p.bio, p.rating, p.ratingCount);
    }

    public EmployerProfileResponse save(Authentication a, EmployerProfileRequest r) {
        EmployerProfile p = p(a);
        p.companyName = r.companyName();
        p.location = r.location();
        p.bio = r.bio();
        repo.save(p);
        return get(a);
    }
}
