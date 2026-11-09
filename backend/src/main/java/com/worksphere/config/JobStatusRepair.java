package com.worksphere.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.worksphere.service.JobService;

/**
 * Keeps stored job statuses in line with the calendar.
 *
 * A job whose date/end time has passed is moved from OPEN or FULL to EXPIRED in the
 * database. It runs once when the application starts (this also repairs jobs that were
 * left as OPEN by older versions) and then every five minutes. Employers also get an
 * on-demand refresh whenever they load their own jobs, so the sweep is only the safety net.
 */
@Component
public class JobStatusRepair {

    private static final Logger log = LoggerFactory.getLogger(JobStatusRepair.class);

    private final JobService jobs;

    public JobStatusRepair(JobService jobs) {
        this.jobs = jobs;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void repairOnStartup() {
        sweep();
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000L, initialDelay = 5 * 60 * 1000L)
    public void repairPeriodically() {
        sweep();
    }

    private void sweep() {
        try {
            int changed = jobs.expirePastJobs();
            if (changed > 0) {
                log.info("Marked {} past job(s) as EXPIRED", changed);
            }
        } catch (RuntimeException e) {
            log.warn("Could not expire past jobs: {}", e.getMessage());
        }
    }
}
