package com.worksphere.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.worksphere.dto.job.JobRequest;
import com.worksphere.entity.Application;
import com.worksphere.entity.ApplicationStatus;
import com.worksphere.entity.Commitment;
import com.worksphere.entity.CommitmentStatus;
import com.worksphere.entity.DepositStatus;
import com.worksphere.entity.Job;
import com.worksphere.entity.JobStatus;
import com.worksphere.entity.NotificationType;
import com.worksphere.entity.Role;
import com.worksphere.entity.User;
import com.worksphere.exception.BadRequestException;
import com.worksphere.repository.ApplicationRepository;
import com.worksphere.repository.CommitmentRepository;
import com.worksphere.repository.EmployerProfileRepository;
import com.worksphere.repository.JobRepository;
import com.worksphere.repository.JobSkillRepository;
import com.worksphere.util.DateTimeUtil;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock JobRepository jobs;
    @Mock JobSkillRepository jobSkills;
    @Mock SkillService skills;
    @Mock UserService users;
    @Mock ApplicationRepository apps;
    @Mock EmployerProfileRepository employers;
    @Mock MatchingService matching;
    @Mock CommitmentRepository commitments;
    @Mock NotificationService notifications;
    @Mock Authentication auth;

    JobService service;
    User employer;

    @BeforeEach
    void setUp() {
        service = new JobService(jobs, jobSkills, skills, users, apps, employers, matching, commitments, notifications);
        employer = new User();
        employer.id = 1L;
        employer.fullName = "Employer One";
        employer.role = Role.EMPLOYER;
        lenient().when(users.current(auth)).thenReturn(employer);
        lenient().when(jobs.save(any(Job.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private static LocalDate today() {
        return DateTimeUtil.now().toLocalDate();
    }

    private Job job(long id, LocalDate date, JobStatus status) {
        Job j = new Job();
        j.id = id;
        j.employer = employer;
        j.title = "Job " + id;
        j.jobDate = date;
        j.startTime = LocalTime.of(9, 0);
        j.endTime = LocalTime.of(17, 0);
        j.pay = 500.0;
        j.status = status;
        return j;
    }

    private JobRequest request(LocalDate date) {
        return new JobRequest("Event Assistant", "Events", "desc", null, "Delhi", date,
                LocalTime.of(9, 0), LocalTime.of(17, 0), 600.0, 2, false, 0.0);
    }

    @Test
    void contextSmoke() {
        assertNotNull(JobService.class);
    }

    // ---- Date passes: OPEN/FULL jobs become EXPIRED ----

    @Test
    void expirePastJobsMovesOnlyPastJobsToExpired() {
        Job past = job(1, today().minusDays(1), JobStatus.OPEN);
        Job pastFull = job(2, today().minusDays(3), JobStatus.FULL);
        Job future = job(3, today().plusDays(2), JobStatus.OPEN);
        when(jobs.findByDeletedFalseAndStatusIn(any())).thenReturn(List.of(past, pastFull, future));

        assertEquals(2, service.expirePastJobs());

        assertEquals(JobStatus.EXPIRED, past.status);
        assertEquals(JobStatus.EXPIRED, pastFull.status);
        assertEquals(JobStatus.OPEN, future.status);
    }

    @Test
    void overnightJobStartingTodayIsNotExpired() {
        Job overnight = job(1, today(), JobStatus.OPEN);
        overnight.startTime = LocalTime.of(20, 0);
        overnight.endTime = LocalTime.of(2, 0);
        when(jobs.findByDeletedFalseAndStatusIn(any())).thenReturn(List.of(overnight));

        assertEquals(0, service.expirePastJobs());
        assertEquals(JobStatus.OPEN, overnight.status);
    }

    @Test
    void mineExpiresPastJobsBeforeReturningThem() {
        Job past = job(1, today().minusDays(1), JobStatus.OPEN);
        when(jobs.findByEmployerIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(past));

        var result = service.mine(auth);

        assertEquals("EXPIRED", result.get(0).status());
    }

    // ---- Delete ----

    @Test
    void deleteMarksJobDeletedAndClosesPendingApplicationsAndCommitments() {
        Job job = job(1, today().plusDays(2), JobStatus.FULL);
        when(jobs.findById(1L)).thenReturn(Optional.of(job));

        User worker = new User();
        worker.id = 7L;
        worker.fullName = "Worker";
        Application pending = new Application();
        pending.job = job;
        pending.worker = worker;
        pending.status = ApplicationStatus.PENDING;
        Application selected = new Application();
        selected.job = job;
        selected.worker = worker;
        selected.status = ApplicationStatus.SELECTED;
        when(apps.findByJobIdOrderByAppliedAtAsc(1L)).thenReturn(List.of(pending, selected));

        Commitment confirmed = new Commitment();
        confirmed.job = job;
        confirmed.worker = worker;
        confirmed.employer = employer;
        confirmed.status = CommitmentStatus.CONFIRMED;
        confirmed.depositStatus = DepositStatus.HELD;
        when(commitments.findByJobIdAndStatusIn(eq(1L), any())).thenReturn(List.of(confirmed));

        service.delete(1L, auth);

        assertTrue(job.deleted);
        assertEquals(JobStatus.CANCELLED, job.status);
        assertEquals(ApplicationStatus.REJECTED, pending.status);
        assertEquals(ApplicationStatus.SELECTED, selected.status);
        assertEquals(CommitmentStatus.CANCELLED, confirmed.status);
        assertEquals(DepositStatus.REFUNDED, confirmed.depositStatus);
        verify(notifications).send(eq(worker), eq(NotificationType.APPLICATION), any());
        verify(notifications).send(eq(worker), eq(NotificationType.COMMITMENT), any());
    }

    @Test
    void deletedJobIsReportedAsDeletedToTheEmployer() {
        Job job = job(1, today().plusDays(2), JobStatus.OPEN);
        job.deleted = true;
        job.status = JobStatus.CANCELLED;
        when(jobs.findByEmployerIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(job));

        assertEquals("DELETED", service.mine(auth).get(0).status());
    }

    @Test
    void completedJobCannotBeDeleted() {
        Job job = job(1, today().minusDays(1), JobStatus.COMPLETED);
        when(jobs.findById(1L)).thenReturn(Optional.of(job));

        assertThrows(BadRequestException.class, () -> service.delete(1L, auth));

        assertFalse(job.deleted);
        assertEquals(JobStatus.COMPLETED, job.status);
    }

    @Test
    void deletingAnAlreadyDeletedJobDoesNothing() {
        Job job = job(1, today().plusDays(2), JobStatus.CANCELLED);
        job.deleted = true;
        when(jobs.findById(1L)).thenReturn(Optional.of(job));

        service.delete(1L, auth);

        verify(notifications, never()).send(any(), any(), any());
    }

    // ---- Update ----

    @Test
    void deletedJobCannotBeEdited() {
        Job job = job(1, today().plusDays(2), JobStatus.CANCELLED);
        job.deleted = true;
        when(jobs.findById(1L)).thenReturn(Optional.of(job));

        assertThrows(BadRequestException.class, () -> service.update(1L, auth, request(today().plusDays(5))));
    }

    @Test
    void movingAnExpiredJobToTheFutureReopensIt() {
        Job job = job(1, today().minusDays(2), JobStatus.EXPIRED);
        when(jobs.findById(1L)).thenReturn(Optional.of(job));

        service.update(1L, auth, request(today().plusDays(5)));

        assertEquals(JobStatus.OPEN, job.status);
        assertEquals(today().plusDays(5), job.jobDate);
        verify(jobs).save(job);
    }

    @Test
    void editingAnExpiredJobKeepsItExpiredIfTheDateIsStillPast() {
        Job job = job(1, today().minusDays(2), JobStatus.EXPIRED);
        when(jobs.findById(1L)).thenReturn(Optional.of(job));

        service.update(1L, auth, request(today().minusDays(1)));

        assertEquals(JobStatus.EXPIRED, job.status);
    }
}
