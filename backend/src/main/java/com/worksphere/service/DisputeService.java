package com.worksphere.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.worksphere.dto.dispute.DisputeRequest;
import com.worksphere.dto.dispute.DisputeResolutionRequest;
import com.worksphere.dto.dispute.DisputeResponse;
import com.worksphere.entity.Commitment;
import com.worksphere.entity.Dispute;
import com.worksphere.entity.DisputeStatus;
import com.worksphere.entity.User;
import com.worksphere.exception.UnauthorizedActionException;
import com.worksphere.repository.CommitmentRepository;
import com.worksphere.repository.DisputeRepository;

@Service
public class DisputeService {

    private final DisputeRepository repo;
    private final CommitmentRepository commitments;
    private final UserService users;

    public DisputeService(DisputeRepository r, CommitmentRepository c, UserService u) {
        repo = r;
        commitments = c;
        users = u;
    }

    public DisputeResponse create(Authentication a, DisputeRequest r) {
        User u = users.current(a);
        Commitment c = commitments.findById(r.commitmentId()).orElseThrow();
        if (!c.worker.id.equals(u.id) && !c.employer.id.equals(u.id)) {
            throw new UnauthorizedActionException("Not part of commitment");
        
        }Dispute d = new Dispute();
        d.commitment = c;
        d.raisedBy = u;
        d.reason = r.reason();
        d.description = r.description();
        return to(repo.save(d));
    }

    public List<DisputeResponse> mine(Authentication a) {
        return repo.findByRaisedByIdOrderByCreatedAtDesc(users.current(a).id).stream().map(this::to).toList();
    }

    public List<DisputeResponse> all() {
        return repo.findAllByOrderByCreatedAtDesc().stream().map(this::to).toList();
    }

    public DisputeResponse resolve(Long id, DisputeResolutionRequest r) {
        Dispute d = repo.findById(id).orElseThrow();
        d.status = DisputeStatus.RESOLVED;
        d.resolutionNote = r.resolutionNote();
        d.resolvedAt = LocalDateTime.now();
        return to(repo.save(d));
    }

    private DisputeResponse to(Dispute d) {
        return new DisputeResponse(d.id, d.commitment.id, d.raisedBy.fullName, d.reason, d.description, d.status.name(), d.resolutionNote, d.createdAt, d.resolvedAt);
    }
}
