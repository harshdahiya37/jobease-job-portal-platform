package com.jobprotal.Job.Ease.controller;

import com.jobprotal.Job.Ease.entity.AppUser;
import com.jobprotal.Job.Ease.entity.Job;
import com.jobprotal.Job.Ease.entity.Role;
import com.jobprotal.Job.Ease.repository.AppUserRepository;
import com.jobprotal.Job.Ease.security.JwtUtil;
import com.jobprotal.Job.Ease.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser extractUser(String authHeader) {
        String jwt = authHeader.substring(7);
        String email = jwtUtil.extractUsername(jwt);
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @PostMapping("/jobs")
    public ResponseEntity<?> createJob(@RequestBody Job job,
                                       @RequestHeader("Authorization") String authHeader) {
        AppUser user = extractUser(authHeader);
        if (user.getRole() != Role.RECRUITER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only recruiters can create jobs.");
        }

        Job savedJob = jobService.createJob(job, user);
        return ResponseEntity.ok(savedJob);
    }

    @GetMapping("jobs")
    public ResponseEntity<List<Job>> getAllJobsByRecruiter() {
        return ResponseEntity.ok(jobService.getJobs());
    }

    @GetMapping("jobs/my-posts")
    public ResponseEntity<List<Job>> getJobsByRecruiter(@AuthenticationPrincipal UserDetails userDetails) {
        String recruiterEmail = userDetails.getUsername(); // Assuming the email is used as a unique identifier
        return ResponseEntity.ok(jobService.getJobsByRecruiter(recruiterEmail));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id,
                                       @RequestBody Job job,
                                       @RequestHeader("Authorization") String authHeader) {
        AppUser user = extractUser(authHeader);
        if (user.getRole() != Role.RECRUITER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only recruiters can update jobs.");
        }

        try {
            Job updatedJob = jobService.updateJob(id, job, user);
            return ResponseEntity.ok(updatedJob);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id,
                                            @RequestHeader("Authorization") String authHeader) {
        AppUser user = extractUser(authHeader);

        try {
            jobService.deleteJob(id, user);
            return ResponseEntity.ok("Successfully Deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/jobs/search")
    public ResponseEntity<List<Job>> searchJobs(@RequestParam(required = false) String title,
                                                @RequestParam(required = false) String location) {
        List<Job> jobs = jobService.searchJobs(title, location);

        if (jobs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(jobs);
    }

}
