package com.worksphere.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.worksphere.entity.Strike;
import com.worksphere.entity.StrikeStatus;
import com.worksphere.entity.User;
import com.worksphere.repository.StrikeRepository;
import com.worksphere.repository.WorkerProfileRepository;

@Service
public class StrikeService {

    private final StrikeRepository repo;
    private final WorkerProfileRepository profiles;

    public StrikeService(StrikeRepository r, WorkerProfileRepository p) {
        repo = r;
        profiles = p;
    }

    public Strike add(User u, String reason) {
        Strike s = new Strike();
        s.user = u;
        s.reason = reason;
        s.status = StrikeStatus.ACTIVE;
        s = repo.save(s);
        profiles.findByUserId(u.id).ifPresent(p -> {
            p.strikeCount = repo.findByUserIdAndStatus(u.id, StrikeStatus.ACTIVE).size();
            profiles.save(p);
        });
        return s;
    }

    public List<Strike> all() {
        return repo.findAllByOrderByCreatedAtDesc();
    }
}
