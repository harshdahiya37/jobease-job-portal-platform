package com.jobprotal.Job.Ease.dto;

import com.jobprotal.Job.Ease.entity.ApplicationStatus;
import com.jobprotal.Job.Ease.payload.CandidateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {
    private Long applicationId;
    private Long jobId;
    private CandidateDTO candidate;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
