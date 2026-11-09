package com.worksphere.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.worksphere.entity.Commitment;
import com.worksphere.entity.CommitmentStatus;
import com.worksphere.entity.DepositStatus;
import com.worksphere.entity.Job;
import com.worksphere.entity.JobStatus;
import com.worksphere.entity.Role;
import com.worksphere.entity.User;
import com.worksphere.repository.CommitmentRepository;
import com.worksphere.repository.RatingRepository;

@ExtendWith(MockitoExtension.class)
class CommitmentServiceTest {

    @Mock CommitmentRepository repo;
    @Mock UserService users;
    @Mock NotificationService notifications;
    @Mock RatingRepository ratings;
    @Mock Authentication auth;

    CommitmentService service;
    User employer;
    Job job;
    Commitment commitment;

    @BeforeEach
    void setUp() {
        service = new CommitmentService(repo, users, notifications, ratings);

        employer = new User();
        employer.id = 1L;
        employer.fullName = "Employer";
        employer.role = Role.EMPLOYER;

        User worker = new User();
        worker.id = 2L;
        worker.fullName = "Worker";
        worker.role = Role.JOB_SEEKER;

        job = new Job();
        job.id = 10L;
        job.employer = employer;
        job.title = "Event Assistant";
        job.status = JobStatus.FULL;

        commitment = new Commitment();
        commitment.id = 5L;
        commitment.job = job;
        commitment.worker = worker;
        commitment.employer = employer;
        commitment.status = CommitmentStatus.CONFIRMED;
        commitment.depositStatus = DepositStatus.NOT_REQUIRED;

        lenient().when(users.current(auth)).thenReturn(employer);
        lenient().when(repo.findByIdAndEmployerId(5L, 1L)).thenReturn(Optional.of(commitment));
    }

    @Test
    void contextSmoke() {
        assertNotNull(CommitmentService.class);
    }

    @Test
    void lastWorkerFinishingCompletesTheJob() {
        when(repo.existsByJobIdAndStatusIn(eq(10L), any())).thenReturn(false);

        service.complete(5L, auth);

        assertEquals(CommitmentStatus.COMPLETED, commitment.status);
        assertEquals(JobStatus.COMPLETED, job.status);
    }

    @Test
    void jobStaysOpenWhileOtherWorkersAreStillWorking() {
        when(repo.existsByJobIdAndStatusIn(eq(10L), any())).thenReturn(true);

        service.complete(5L, auth);

        assertEquals(CommitmentStatus.COMPLETED, commitment.status);
        assertEquals(JobStatus.FULL, job.status);
    }
}
