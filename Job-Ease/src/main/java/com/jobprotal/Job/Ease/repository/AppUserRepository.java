package com.jobprotal.Job.Ease.repository;

import com.jobprotal.Job.Ease.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);
    List<AppUser> findByEnabledFalse();

    @Query("SELECT COUNT(u) FROM AppUser u")
    long countTotalUsers();

    @Query("SELECT COUNT(u) FROM AppUser u WHERE u.role = 'RECRUITER'")
    long countRecruiters();

    @Query("SELECT COUNT(u) FROM AppUser u WHERE u.role = 'CANDIDATE'")
    long countCandidates();

}
