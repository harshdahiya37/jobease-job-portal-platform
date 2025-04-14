package com.jobprotal.Job.Ease.service;

import com.jobprotal.Job.Ease.entity.AppUser;
import com.jobprotal.Job.Ease.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private AppUserRepository userRepository;

    public AppUser registerUser(AppUser user) {
        return userRepository.save(user);
    }

    // ✅ This method is required by Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}



