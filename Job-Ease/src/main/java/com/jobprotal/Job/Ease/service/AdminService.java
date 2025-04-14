package com.jobprotal.Job.Ease.service;

import com.jobprotal.Job.Ease.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private AppUserRepository appUserRepository;

    public Map<String, Long> getUserStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", appUserRepository.countTotalUsers());
        stats.put("totalRecruiters", appUserRepository.countRecruiters());
        stats.put("totalCandidates", appUserRepository.countCandidates());
        return stats;
    }
}
