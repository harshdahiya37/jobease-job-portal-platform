package com.jobprotal.Job.Ease.payload;

import com.jobprotal.Job.Ease.entity.Job;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminJobDTO {
    private Long jobId;
    private String title;
    private String description;
    private String location;
    private String recruiterName;
    private String recruiterEmail;

    public static AdminJobDTO from(Job job) {
        return new AdminJobDTO(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getLocation(),
                job.getRecruiter().getName(),
                job.getRecruiter().getEmail()
        );
    }
}
