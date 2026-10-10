package com.worksphere.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.worksphere.dto.profile.WorkerProfileRequest;
import com.worksphere.dto.profile.WorkerProfileResponse;
import com.worksphere.entity.WorkerProfile;
import com.worksphere.entity.WorkerSkill;
import com.worksphere.exception.ResourceNotFoundException;
import com.worksphere.repository.WorkerProfileRepository;
import com.worksphere.repository.WorkerSkillRepository;

@Service
public class WorkerProfileService {

    private final WorkerProfileRepository profiles;
    private final WorkerSkillRepository links;
    private final SkillService skills;
    private final UserService users;

    public WorkerProfileService(WorkerProfileRepository p, WorkerSkillRepository l, SkillService s, UserService u) {
        profiles = p;
        links = l;
        skills = s;
        users = u;
    }

    public WorkerProfile profile(Authentication a) {
        return profiles.findByUserId(users.current(a).id).orElseThrow(() -> new ResourceNotFoundException("Worker profile not found"));
    }

    public WorkerProfileResponse get(Authentication a) {
        WorkerProfile p = profile(a);
        return new WorkerProfileResponse(p.id, p.user.id, p.user.fullName, p.location, p.bio, p.availability, p.rating, p.ratingCount, p.strikeCount, links.findByWorkerId(p.id).stream().map(x -> x.skill.name).toList());
    }

    public WorkerProfileResponse save(Authentication a, WorkerProfileRequest r) {
        WorkerProfile p = profile(a);
        p.location = r.location();
        p.bio = r.bio();
        p.availability = r.availability();
        links.deleteByWorkerId(p.id);
        if (r.skills() != null) {
            for (String s : r.skills()) {
                WorkerSkill w = new WorkerSkill();
                w.worker = p;
                w.skill = skills.getOrCreate(s);
                links.save(w);
            }
        
        }profiles.save(p);
        return get(a);
    }
}
