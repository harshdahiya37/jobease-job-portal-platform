package com.jobprotal.Job.Ease.controller;

import com.jobprotal.Job.Ease.entity.*;
import com.jobprotal.Job.Ease.repository.*;
import com.jobprotal.Job.Ease.security.JwtUtil;
import com.jobprotal.Job.Ease.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil;

    // ✅ CANDIDATE applies to job
    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/apply")
    public ResponseEntity<?> applyToJob(@RequestHeader("Authorization") String authHeader,
                                        @RequestParam Long jobId) {
        AppUser user = extractUserFromToken(authHeader);
        return ResponseEntity.ok(applicationService.applyToJob(user, jobId));
    }

    // ✅ RECRUITER updates application status
    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long applicationId,
                                                     @RequestParam ApplicationStatus newStatus,
                                                     @RequestHeader("Authorization") String authHeader) {
        AppUser user = extractUserFromToken(authHeader);
        try {
            Application updatedApp = applicationService.updateApplicationStatus(applicationId, newStatus, user.getEmail());
            return ResponseEntity.ok(updatedApp);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ✅ CANDIDATE sees their applications
    @GetMapping("/my")
    public ResponseEntity<?> getOwnApplications(@RequestHeader("Authorization") String authHeader) {
        AppUser user = extractUserFromToken(authHeader);
        if (user.getRole() != Role.CANDIDATE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only candidates can view their applications.");
        }
        return ResponseEntity.ok(applicationService.getApplicationsByUser(user.getId()));
    }

    // ✅ RECRUITER views applications for job they posted
    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<?> getApplicationsByJob(@PathVariable Long jobId,
                                                  @RequestHeader("Authorization") String authHeader) {
        AppUser user = extractUserFromToken(authHeader);
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId, user.getEmail()));
    }

    // ✅ CANDIDATE deletes their own application
    @PreAuthorize("hasRole('CANDIDATE')")
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<?> deleteApplication(@PathVariable Long applicationId,
                                               @RequestHeader("Authorization") String authHeader) {
        AppUser user = extractUserFromToken(authHeader);
        try {
            applicationService.deleteApplication(applicationId, user);
            return ResponseEntity.ok("Application deleted.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // 🔐 Utility
    private AppUser extractUserFromToken(String authHeader) {
        String jwt = authHeader.substring(7);
        String email = jwtUtil.extractUsername(jwt);
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

