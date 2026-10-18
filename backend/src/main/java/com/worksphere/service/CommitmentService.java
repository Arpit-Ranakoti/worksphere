package com.worksphere.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.worksphere.dto.commitment.CommitmentResponse;
import com.worksphere.entity.Commitment;
import com.worksphere.entity.CommitmentStatus;
import com.worksphere.entity.DepositStatus;
import com.worksphere.entity.JobStatus;
import com.worksphere.entity.NotificationType;
import com.worksphere.entity.Role;
import com.worksphere.entity.User;
import com.worksphere.exception.BadRequestException;
import com.worksphere.exception.UnauthorizedActionException;
import com.worksphere.repository.CommitmentRepository;
import com.worksphere.repository.RatingRepository;

@Service
public class CommitmentService {

    private final CommitmentRepository repo;
    private final UserService users;
    private final NotificationService notifications;
    private final RatingRepository ratings;   // NEW

    public CommitmentService(CommitmentRepository r, UserService u, NotificationService n, RatingRepository ratings) {   // NEW param
        repo = r;
        users = u;
        notifications = n;
        this.ratings = ratings;   // NEW
    }

    public List<CommitmentResponse> mine(Authentication a) {
        User u = users.current(a);
        return (u.role == Role.EMPLOYER ? repo.findByEmployerIdOrderByIdDesc(u.id) : repo.findByWorkerIdOrderByIdDesc(u.id)).stream().map(c -> to(c, u)).toList();
    }

    private Commitment get(Long id, Authentication a) {
        User u = users.current(a);
        Optional<Commitment> x = u.role == Role.EMPLOYER ? repo.findByIdAndEmployerId(id, u.id) : repo.findByIdAndWorkerId(id, u.id);
        return x.orElseThrow(() -> new UnauthorizedActionException("Commitment not found or not yours"));
    }

    public void confirm(Long id, Authentication a) {
        Commitment c = get(id, a);
        if (c.status != CommitmentStatus.PENDING_CONFIRMATION) throw new BadRequestException("Cannot confirm this commitment");
        c.status = CommitmentStatus.CONFIRMED;
        c.confirmedAt = java.time.LocalDateTime.now();
        if (c.depositStatus == DepositStatus.PENDING) c.depositStatus = DepositStatus.HELD;
        repo.save(c);
        notifications.send(c.employer, NotificationType.COMMITMENT, c.worker.fullName + " confirmed the commitment for " + c.job.title);
    }

    public void decline(Long id, Authentication a) {
        Commitment c = get(id, a);
        c.status = CommitmentStatus.DECLINED;
        repo.save(c);
        notifications.send(c.employer, NotificationType.COMMITMENT, c.worker.fullName + " declined the commitment for " + c.job.title);
    }

    public void cancel(Long id, Authentication a, String reason) {
        Commitment c = get(id, a);
        c.status = CommitmentStatus.CANCELLED;
        c.cancelReason = reason;
        c.depositStatus = c.depositStatus == DepositStatus.HELD ? DepositStatus.FORFEITED : c.depositStatus;
        repo.save(c);
        notifications.send(c.worker, NotificationType.COMMITMENT, "Commitment for " + c.job.title + " was cancelled");
        notifications.send(c.employer, NotificationType.COMMITMENT, "Commitment for " + c.job.title + " was cancelled");
    }

    public void complete(Long id, Authentication a) {
        Commitment c = get(id, a);
        if (c.status != CommitmentStatus.CONFIRMED) throw new BadRequestException("Only confirmed commitments can complete");
        c.status = CommitmentStatus.COMPLETED;
        c.completedAt = java.time.LocalDateTime.now();
        if (c.depositStatus == DepositStatus.HELD) c.depositStatus = DepositStatus.REFUNDED;
        c.job.status = JobStatus.COMPLETED;
        repo.save(c);
        notifications.send(c.worker, NotificationType.COMMITMENT, "Job completed: " + c.job.title);
    }

    public List<CommitmentResponse> upcoming(Authentication a) {
        return mine(a).stream().filter(c -> c.jobDate() != null && c.jobDate().compareTo(java.time.LocalDate.now()) >= 0).toList();
    }

    private CommitmentResponse to(Commitment c, User me) {
        boolean worker = me.role == Role.JOB_SEEKER;
        Long otherId = worker ? c.employer.id : c.worker.id;
        boolean ratedByMe = ratings.findByCommitmentIdAndFromUserIdAndToUserId(c.id, me.id, otherId).isPresent();   // NEW
        return new CommitmentResponse(c.id, c.job.id, c.job.title, c.job.location, c.job.jobDate,
                c.job.startTime == null ? null : c.job.startTime.toString(),
                c.job.endTime == null ? null : c.job.endTime.toString(),
                c.worker.fullName, c.employer.fullName, c.status.name(), c.depositStatus.name(), c.cancelReason, c.job.pay,
                worker ? c.employer.fullName : c.worker.fullName, otherId, ratedByMe);   // was: false
    }
}