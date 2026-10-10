package com.worksphere.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.worksphere.entity.Job;
import com.worksphere.entity.User;
import com.worksphere.entity.WorkerProfile;
import com.worksphere.repository.JobSkillRepository;
import com.worksphere.repository.WorkerProfileRepository;
import com.worksphere.repository.WorkerSkillRepository;
import com.worksphere.util.LocationMatcher;

@Service
public class MatchingService {

    private final WorkerProfileRepository wp;
    private final WorkerSkillRepository ws;
    private final JobSkillRepository js;
    private final LocationMatcher loc;

    public MatchingService(WorkerProfileRepository w, WorkerSkillRepository s, JobSkillRepository j, LocationMatcher l) {
        wp = w;
        ws = s;
        js = j;
        loc = l;
    }

    public double score(Job job, User worker) {
        WorkerProfile p = wp.findByUserId(worker.id).orElse(null);
        if (p == null) {
            return 0;
        
        }Set<String> workerSkills = new HashSet<>(ws.findByWorkerId(p.id).stream().map(x -> x.skill.name.toLowerCase()).toList());
        List<String> req = js.findByJobId(job.id).stream().map(x -> x.skill.name.toLowerCase()).toList();
        double skill = req.isEmpty() ? 100 : req.stream().filter(workerSkills::contains).count() * 100.0 / req.size();
        double location = loc.score(p.location, job.location);
        return Math.round(skill * 0.7 + location * 0.3);
    }
}
