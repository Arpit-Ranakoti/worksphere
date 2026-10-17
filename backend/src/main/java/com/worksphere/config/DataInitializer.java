package com.worksphere.config;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.worksphere.entity.EmployerProfile;
import com.worksphere.entity.Job;
import com.worksphere.entity.JobSkill;
import com.worksphere.entity.JobStatus;
import com.worksphere.entity.Role;
import com.worksphere.entity.Skill;
import com.worksphere.entity.User;
import com.worksphere.entity.WorkerProfile;
import com.worksphere.repository.EmployerProfileRepository;
import com.worksphere.repository.JobRepository;
import com.worksphere.repository.JobSkillRepository;
import com.worksphere.repository.SkillRepository;
import com.worksphere.repository.UserRepository;
import com.worksphere.repository.WorkerProfileRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(UserRepository users, WorkerProfileRepository wp, EmployerProfileRepository ep, SkillRepository skills, JobRepository jobs, JobSkillRepository js, PasswordEncoder enc) {
        return args -> {
            if (users.count() > 0) {
                return;
            
            }User admin = new User();
            admin.fullName = "WorkSphere Admin";
            admin.email = "admin@worksphere.com";
            admin.password = enc.encode("Admin@123");
            admin.role = Role.ADMIN;
            users.save(admin);
            User employer = new User();
            employer.fullName = "Demo Employer";
            employer.email = "employer1@worksphere.com";
            employer.password = enc.encode("Employer@123");
            employer.role = Role.EMPLOYER;
            users.save(employer);
            EmployerProfile ep1 = new EmployerProfile();
            ep1.user = employer;
            ep1.companyName = "Demo Employer";
            ep1.location = "Dehradun";
            ep.save(ep1);
            for (int i = 1; i <= 3; i++) {
                User w = new User();
                w.fullName = "Worker " + i;
                w.email = "worker" + i + "@worksphere.com";
                w.password = enc.encode("Worker@123");
                w.role = Role.JOB_SEEKER;
                users.save(w);
                WorkerProfile p = new WorkerProfile();
                p.user = w;
                p.location = i == 1 ? "Dehradun" : i == 2 ? "Rishikesh" : "Haridwar";
                wp.save(p);
            }
            String[] names = {"Events", "Delivery", "Data Entry", "Customer Service", "Cleaning", "Marketing"};
            for (String n : names) {
                if (skills.findByNameIgnoreCase(n).isEmpty()) { skills.save(new Skill(n)); }
            
            }Job j = new Job();
            j.employer = employer;
            j.title = "Event Assistant";
            j.category = "Events";
            j.description = "Help with event setup, guest support and cleanup.";
            j.location = "Dehradun";
            j.jobDate = LocalDate.now().plusDays(2);
            j.startTime = LocalTime.of(10, 0);
            j.endTime = LocalTime.of(18, 0);
            j.pay = 800.0;
            j.workersRequired = 2;
            j.status = JobStatus.OPEN;
            jobs.save(j);
            JobSkill link = new JobSkill();
            link.job = j;
            link.skill = skills.findByNameIgnoreCase("Events").orElseThrow();
            js.save(link);
        };
    }
}
