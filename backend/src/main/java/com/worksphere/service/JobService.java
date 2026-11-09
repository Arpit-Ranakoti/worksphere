package com.worksphere.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.worksphere.dto.job.JobRequest;
import com.worksphere.dto.job.JobResponse;
import com.worksphere.dto.job.JobSearchFilter;
import com.worksphere.entity.Application;
import com.worksphere.entity.ApplicationStatus;
import com.worksphere.entity.Commitment;
import com.worksphere.entity.CommitmentStatus;
import com.worksphere.entity.DepositStatus;
import com.worksphere.entity.Job;
import com.worksphere.entity.JobSkill;
import com.worksphere.entity.JobStatus;
import com.worksphere.entity.NotificationType;
import com.worksphere.entity.Role;
import com.worksphere.entity.User;
import com.worksphere.exception.BadRequestException;
import com.worksphere.exception.ResourceNotFoundException;
import com.worksphere.exception.UnauthorizedActionException;
import com.worksphere.repository.ApplicationRepository;
import com.worksphere.repository.CommitmentRepository;
import com.worksphere.repository.EmployerProfileRepository;
import com.worksphere.repository.JobRepository;
import com.worksphere.repository.JobSkillRepository;
import com.worksphere.util.DateTimeUtil;

@Service
public class JobService {

    /** Statuses a job can still be in while its date has not been handled yet. */
    private static final List<JobStatus> LIVE_STATUSES = List.of(JobStatus.OPEN, JobStatus.FULL);

    /** Commitments that are still waiting to be worked or completed. */
    private static final List<CommitmentStatus> ACTIVE_COMMITMENTS = List.of(
            CommitmentStatus.PENDING_CONFIRMATION, CommitmentStatus.PENDING, CommitmentStatus.CONFIRMED);

    private final JobRepository jobs;
    private final JobSkillRepository jobSkills;
    private final SkillService skills;
    private final UserService users;
    private final ApplicationRepository apps;
    private final EmployerProfileRepository employers;
    private final MatchingService matching;
    private final CommitmentRepository commitments;
    private final NotificationService notifications;

    public JobService(JobRepository j, JobSkillRepository js, SkillService s, UserService u, ApplicationRepository a, EmployerProfileRepository e, MatchingService m, CommitmentRepository c, NotificationService n) {
        jobs = j;
        jobSkills = js;
        skills = s;
        users = u;
        apps = a;
        employers = e;
        matching = m;
        commitments = c;
        notifications = n;
    }

    public List<JobResponse> search(JobSearchFilter f, Authentication a) {
        User worker = a == null ? null : users.current(a);
        return jobs.findAllByOrderByCreatedAtDesc().stream().filter(this::isActive).filter(j -> f.keyword() == null || f.keyword().isBlank() || j.title.toLowerCase().contains(f.keyword().toLowerCase())).filter(j -> f.location() == null || f.location().isBlank() || j.location.toLowerCase().contains(f.location().toLowerCase())).filter(j -> f.category() == null || f.category().isBlank() || j.category.equalsIgnoreCase(f.category())).filter(j -> f.date() == null || f.date().isBlank() || j.jobDate.toString().equals(f.date())).filter(j -> f.minPay() == null || j.pay >= f.minPay()).filter(j -> f.maxPay() == null || j.pay <= f.maxPay()).map(j -> to(j, worker)).sorted((x, y) -> {
            if (Boolean.TRUE.equals(f.recommended()) && worker != null) {
                return Double.compare(Optional.ofNullable(y.matchScore()).orElse(0.0), Optional.ofNullable(x.matchScore()).orElse(0.0));
            
            }return 0;
        }).toList();
    }

    public JobResponse get(Long id, Authentication a) {
        Job j = jobs.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        User viewer = a == null ? null : users.current(a);
        boolean owner = viewer != null && viewer.role == Role.EMPLOYER && j.employer.id.equals(viewer.id);
        if (j.deleted && !owner) {
            throw new ResourceNotFoundException("Job not found");
        }
        if (j.status == JobStatus.COMPLETED && !owner) {
            throw new ResourceNotFoundException("Job not found");
        }
        return to(j, viewer);
    }

    @Transactional
    public List<JobResponse> mine(Authentication a) {
        User u = users.current(a);
        // Full job history for the employer: active, expired, completed and deleted jobs.
        // Deleted jobs come back with status DELETED. Jobs whose date has passed are moved
        // to EXPIRED here first, so the stored status is already correct when the Dashboard
        // and My Jobs pages read it, without waiting for the scheduled sweep.
        List<Job> mine = jobs.findByEmployerIdOrderByCreatedAtDesc(u.id);
        mine.forEach(this::expireIfPast);
        return mine.stream().map(x -> to(x, u)).toList();
    }

    /**
     * Moves every live job whose date/end time has passed to EXPIRED. Called by the
     * scheduled sweep and once at startup, which also repairs rows that were left as
     * OPEN or FULL by older versions. Returns how many jobs were changed.
     */
    @Transactional
    public int expirePastJobs() {
        int changed = 0;
        for (Job j : jobs.findByDeletedFalseAndStatusIn(LIVE_STATUSES)) {
            if (expireIfPast(j)) {
                changed++;
            }
        }
        return changed;
    }

    private boolean expireIfPast(Job j) {
        if (!j.deleted && LIVE_STATUSES.contains(j.status) && DateTimeUtil.isPast(j.jobDate, j.startTime, j.endTime)) {
            j.status = JobStatus.EXPIRED;
            jobs.save(j);
            return true;
        }
        return false;
    }

    /**
     * A job is shown to workers when it is not deleted, still OPEN (workers cannot
     * apply to FULL, EXPIRED, COMPLETED or CANCELLED jobs), and its date/end time has not passed.
     */
    private boolean isActive(Job j) {
        return !j.deleted
                && j.status == JobStatus.OPEN
                && !DateTimeUtil.isPast(j.jobDate, j.startTime, j.endTime);
    }

    public JobResponse create(Authentication a, JobRequest r) {
        User u = users.current(a);
        if (u.role != Role.EMPLOYER) {
            throw new UnauthorizedActionException("Only employers can post jobs");
        
        }Job j = new Job();
        j.employer = u;
        apply(j, r);
        j = jobs.save(j);
        saveSkills(j, r.requiredSkills());
        return to(j, u);
    }

    @Transactional
    public JobResponse update(Long id, Authentication a, JobRequest r) {
        Job j = owned(id, a);
        if (j.deleted) {
            throw new BadRequestException("A deleted job cannot be edited");
        }
        if (j.status == JobStatus.COMPLETED) {
            throw new BadRequestException("A completed job cannot be edited");
        }
        apply(j, r);
        // Moving an expired job to a future date brings it back to life.
        if (j.status == JobStatus.EXPIRED && !DateTimeUtil.isPast(j.jobDate, j.startTime, j.endTime)) {
            j.status = j.workersSelected >= j.workersRequired ? JobStatus.FULL : JobStatus.OPEN;
        }
        j = jobs.save(j);
        jobSkills.deleteByJobId(j.id);
        saveSkills(j, r.requiredSkills());
        return to(j, users.current(a));
    }

    /**
     * Soft-deletes a job. It leaves the Dashboard and the public Jobs page but stays in
     * My Jobs with status DELETED. Everything that depended on the job is closed so
     * nothing is left pointing at it: pending applications are rejected and unfinished
     * commitments are cancelled (held deposits are refunded), and workers are told.
     */
    @Transactional
    public void delete(Long id, Authentication a) {
        Job job = owned(id, a);
        if (job.deleted) {
            return;
        }
        if (job.status == JobStatus.COMPLETED) {
            throw new BadRequestException("A completed job cannot be deleted");
        }
        job.deleted = true;
        job.status = JobStatus.CANCELLED;
        jobs.save(job);

        for (Application ap : apps.findByJobIdOrderByAppliedAtAsc(job.id)) {
            if (ap.status == ApplicationStatus.PENDING) {
                ap.status = ApplicationStatus.REJECTED;
                apps.save(ap);
                notifications.send(ap.worker, NotificationType.APPLICATION,
                        "Your application for " + job.title + " was closed because the employer deleted the job");
            }
        }
        for (Commitment c : commitments.findByJobIdAndStatusIn(job.id, ACTIVE_COMMITMENTS)) {
            c.status = CommitmentStatus.CANCELLED;
            c.cancelReason = "The employer deleted the job";
            if (c.depositStatus == DepositStatus.HELD) {
                c.depositStatus = DepositStatus.REFUNDED;
            }
            commitments.save(c);
            notifications.send(c.worker, NotificationType.COMMITMENT,
                    "Your commitment for " + job.title + " was cancelled because the employer deleted the job");
        }
    }

    private Job owned(Long id, Authentication a) {
        Job j = jobs.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (!j.employer.id.equals(users.current(a).id)) {
            throw new UnauthorizedActionException("Not your job");
        
        }return j;
    }

    private void apply(Job j, JobRequest r) {
        j.title = r.title();
        j.category = r.category();
        j.description = r.description();
        j.location = r.location();
        j.jobDate = r.jobDate();
        j.startTime = r.startTime();
        j.endTime = r.endTime();
        j.pay = r.pay();
        j.workersRequired = r.workersRequired() == null ? 1 : r.workersRequired();
        j.depositRequired = Boolean.TRUE.equals(r.depositRequired());
        j.depositAmount = r.depositAmount() == null ? 0 : r.depositAmount();
    }

    private void saveSkills(Job j, List<String> names) {
        if (names != null) {
            for (String n : names) {
                JobSkill x = new JobSkill();
                x.job = j;
                x.skill = skills.getOrCreate(n);
                jobSkills.save(x);
            }
    
        }}

    private JobResponse to(Job j, User viewer) {
        Double score = null;
        Boolean applied = false;
        if (viewer != null && viewer.role == Role.JOB_SEEKER) {
            score = matching.score(j, viewer);
            applied = apps.findByJobIdAndWorkerId(j.id, viewer.id).isPresent();
        }
        double rating = employers.findByUserId(j.employer.id).map(p -> p.rating).orElse(0.0);
        return new JobResponse(j.id, j.employer.id, j.employer.fullName, j.title, j.category, j.description, jobSkills.findByJobId(j.id).stream().map(x -> x.skill.name).toList(), j.location, j.jobDate, j.startTime, j.endTime, j.pay, j.workersRequired, j.workersSelected, j.depositRequired, j.depositAmount, j.deleted ? "DELETED" : j.status.name(), score, applied, rating, (int) apps.countByJobId(j.id));
    }
}