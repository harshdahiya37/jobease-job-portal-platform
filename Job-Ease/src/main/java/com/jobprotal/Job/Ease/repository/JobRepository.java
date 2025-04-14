package com.jobprotal.Job.Ease.repository;

import com.jobprotal.Job.Ease.entity.Job;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Get all non-deleted jobs with recruiter eagerly loaded (optional but helpful)
    @Query("SELECT j FROM Job j JOIN FETCH j.recruiter WHERE j.deleted = false")
    List<Job> findAllWithRecruiter();

    @Modifying
    @Transactional
    @Query("UPDATE Job j SET j.deleted = true WHERE j.id = :jobId")
    void softDeleteJob(@Param("jobId") Long jobId);

    @Query("SELECT j FROM Job j WHERE (:title IS NULL OR j.title LIKE %:title%) AND (:location IS NULL OR j.location LIKE %:location%)")
    List<Job> findByTitleAndLocation(@Param("title") String title, @Param("location") String location);

    @Query("SELECT j FROM Job j WHERE j.recruiter.email = :email")
    List<Job> findByRecruiterEmail(@Param("email") String email);

}
