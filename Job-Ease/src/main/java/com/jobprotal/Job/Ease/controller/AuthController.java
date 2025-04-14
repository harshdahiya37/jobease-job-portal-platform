package com.jobprotal.Job.Ease.controller;

import com.jobprotal.Job.Ease.dto.AuthResponse;
import com.jobprotal.Job.Ease.dto.LoginRequest;
import com.jobprotal.Job.Ease.dto.LoginResponse;
import com.jobprotal.Job.Ease.entity.AppUser;
import com.jobprotal.Job.Ease.entity.Role;
import com.jobprotal.Job.Ease.payload.RegisterRequest;
import com.jobprotal.Job.Ease.repository.AppUserRepository;
import com.jobprotal.Job.Ease.service.UserService;
import com.jobprotal.Job.Ease.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AppUserRepository appUserRepository;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          @Qualifier("customUserDetailsService")
                          UserDetailsService userDetailsService,
                          AppUserRepository appUserRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.appUserRepository = appUserRepository;
    }
    
    // 🔐 Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        Role userRole = Role.CANDIDATE; // default
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            userRole = Role.valueOf(request.getRole().toUpperCase());
        }

        AppUser newUser = new AppUser();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(userRole); // ✅ Don't forget this!

        appUserRepository.save(newUser);

        // Load user details and generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getEmail());
        String jwtToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    // 🔑 Authenticate and login a user
    @PostMapping("/login")
    public LoginResponse loginUser(@RequestBody LoginRequest request) {
        // Validate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Load user details and generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return new LoginResponse(token, "Login successful");
    }
}


