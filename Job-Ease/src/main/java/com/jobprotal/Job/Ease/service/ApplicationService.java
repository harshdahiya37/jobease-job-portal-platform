package com.jobprotal.Job.Ease.service;

import com.jobprotal.Job.Ease.dto.ApplicationResponse;
import com.jobprotal.Job.Ease.entity.*;
import com.jobprotal.Job.Ease.payload.CandidateDTO;
import com.jobprotal.Job.Ease.repository.ApplicationRepository;
import com.jobprotal.Job.Ease.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    @Autowired
    private final ApplicationRepository applicationRepository;

    @Autowired
    private final JobRepository jobRepository;

    // ✅ Candidate applies to a job
    public Application applyToJob(AppUser user, Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationRepository.existsByUserIdAndJobId(user.getId(), jobId)) {
            throw new RuntimeException("You already applied to this job.");
        }

        Application application = Application.builder()
                .user(user)
                .job(job)
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();

        return applicationRepository.save(application);
    }

    // ✅ Recruiter updates status
    @Transactional
    public Application updateApplicationStatus(Long applicationId, ApplicationStatus newStatus, String recruiterEmail) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Job job = application.getJob();
        if (!job.getRecruiter().getEmail().equals(recruiterEmail)) {
            throw new RuntimeException("You are not authorized to update this application.");
        }

        application.setStatus(newStatus);
        return applicationRepository.save(application);
    }

    // ✅ Candidate fetches their applications
    public List<Application> getApplicationsByUser(Long userId) {
        return applicationRepository.findByUserId(userId);
    }

    // ✅ Recruiter fetches applications for their job
    public List<ApplicationResponse> getApplicationsByJob(Long jobId, String recruiterEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getRecruiter().getEmail().equals(recruiterEmail)) {
            throw new RuntimeException("Unauthorized to view applications for this job");
        }

        List<Application> applications = applicationRepository.findByJobId(jobId);

        return applications.stream().map(app -> {
            ApplicationResponse dto = new ApplicationResponse();
            dto.setApplicationId(app.getId());
            dto.setJobId(app.getJob().getId());
            dto.setCandidate(CandidateDTO.fromEntity(app.getCandidate()));
            dto.setStatus(app.getStatus());
            dto.setAppliedAt(app.getAppliedAt());
            return dto;
        }).toList();
    }

    // ✅ Candidate deletes their application
    public void deleteApplication(Long applicationId, AppUser user) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!app.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this application.");
        }

        applicationRepository.delete(app);
    }
}