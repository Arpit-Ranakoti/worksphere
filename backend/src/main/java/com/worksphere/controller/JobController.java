package com.worksphere.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.worksphere.dto.job.JobRequest;
import com.worksphere.dto.job.JobResponse;
import com.worksphere.dto.job.JobSearchFilter;
import com.worksphere.service.ApplicationService;
import com.worksphere.service.JobService;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService s;
    private final ApplicationService applicationService;

    public JobController(JobService x, ApplicationService appService) {
        s = x;
        applicationService = appService;
    }

    @GetMapping
    public Object search(@RequestParam(required = false) String keyword,
                         @RequestParam(required = false) String location,
                         @RequestParam(required = false) String category,
                         @RequestParam(required = false) String date,
                         @RequestParam(required = false) Double minPay,
                         @RequestParam(required = false) Double maxPay,
                         @RequestParam(required = false) Boolean recommended,
                         Authentication a) {
        return s.search(new JobSearchFilter(keyword, location, category, date, minPay, maxPay, recommended), a);
    }

    @GetMapping("/my")
    public Object mine(Authentication a) {
        return s.mine(a);
    }

    @GetMapping("/{id}")
    public JobResponse get(@PathVariable Long id, Authentication a) {
        return s.get(id, a);
    }

    // FIXED: ApplicationService is injected in the constructor, not taken as a method parameter
    @GetMapping("/{jobId}/applications")
    public Object applicants(@PathVariable Long jobId, Authentication a) {
        return applicationService.forJob(jobId, a);
    }

    @PostMapping
    public JobResponse create(Authentication a, @RequestBody JobRequest r) {
        return s.create(a, r);
    }

    @PutMapping("/{id}")
    public JobResponse update(@PathVariable Long id, Authentication a, @RequestBody JobRequest r) {
        return s.update(id, a, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication a) {
        s.delete(id, a);
        return ResponseEntity.noContent().build();
    }
}