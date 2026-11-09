package com.worksphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WorkSphereApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkSphereApplication.class, args);
    }
}
