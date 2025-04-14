package com.jobprotal.Job.Ease.service;

import com.jobprotal.Job.Ease.entity.AppUser;
import com.jobprotal.Job.Ease.entity.Job;
import com.jobprotal.Job.Ease.entity.Role;
import com.jobprotal.Job.Ease.repository.ApplicationRepository;
import com.jobprotal.Job.Ease.repository.JobRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Primary
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public Job createJob(Job job, AppUser recruiter) {
        if (job == null || job.getTitle() == null || job.getDescription() == null) {
            throw new IllegalArgumentException("Job title and description must not be null");
        }

        job.setRecruiter(recruiter);
        return jobRepository.save(job);
    }

    public List<Job> getJobs() {
        return jobRepository.findAll();
    }

    public List<Job> getJobsByRecruiter(String recruiterEmail) {
        return jobRepository.findByRecruiterEmail(recruiterEmail);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    public Job updateJob(Long id, Job job, AppUser recruiter) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        if (!existingJob.getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("You are not authorized to update this job.");
        }

        existingJob.setTitle(job.getTitle());
        existingJob.setDescription(job.getDescription());
        existingJob.setLocation(job.getLocation());
        existingJob.setSkills(job.getSkills());

        return jobRepository.save(existingJob);
    }

    @Transactional
    public void deleteJob(Long id, AppUser user) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        if (user.getRole() == Role.ADMIN || existingJob.getRecruiter().getId().equals(user.getId())) {
            // 🔥 First delete all applications for this job
            applicationRepository.deleteByJobId(existingJob.getId());

            // Then delete the job
            jobRepository.delete(existingJob);
        } else {
            throw new RuntimeException("You are not authorized to delete this job.");
        }
    }

    public List<Job> searchJobs(String title, String location) {
        return jobRepository.findByTitleAndLocation(title, location);
    }

}
