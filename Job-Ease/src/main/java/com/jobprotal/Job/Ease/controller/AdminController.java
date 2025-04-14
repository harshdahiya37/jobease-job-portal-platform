package com.jobprotal.Job.Ease.controller;

import com.jobprotal.Job.Ease.entity.AppUser;
import com.jobprotal.Job.Ease.entity.Job;
import com.jobprotal.Job.Ease.payload.AdminJobDTO;
import com.jobprotal.Job.Ease.payload.UserDTO;
import com.jobprotal.Job.Ease.repository.AppUserRepository;
import com.jobprotal.Job.Ease.repository.JobRepository;
import com.jobprotal.Job.Ease.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private final AppUserRepository appUserRepository;

    @Autowired
    private final JobRepository jobRepository;

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Long>> getAdminDashboard(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(adminService.getUserStatistics());
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = appUserRepository.findAll()
                .stream()
                .map(UserDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            return ResponseEntity.badRequest().body("User already disabled.");
        }

        user.setEnabled(false);
        appUserRepository.save(user);

        return ResponseEntity.ok("User disabled successfully.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/enable")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body("User is already active.");
        }

        user.setEnabled(true);
        appUserRepository.save(user);

        return ResponseEntity.ok("User enabled successfully.");
    }

    @GetMapping("/users/active")
    public ResponseEntity<List<AppUser>> getAllActiveUsers() {
        return ResponseEntity.ok(
                appUserRepository.findAll()
                        .stream()
                        .filter(AppUser::isEnabled)
                        .toList()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/disabled")
    public ResponseEntity<List<AppUser>> getAllDisabledUsers() {
        List<AppUser> disabledUsers = appUserRepository.findByEnabledFalse();
        return ResponseEntity.ok(disabledUsers);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/jobs")
    public ResponseEntity<List<AdminJobDTO>> getAllJobsWithRecruiters() {
        List<Job> jobs = jobRepository.findAllWithRecruiter();
        List<AdminJobDTO> jobDTOs = jobs.stream()
                .map(AdminJobDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(jobDTOs);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<?> deleteJobByAdmin(@PathVariable Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found.");
        }
        jobRepository.softDeleteJob(jobId);
        return ResponseEntity.ok("Job soft-deleted by Admin.");
    }
}

