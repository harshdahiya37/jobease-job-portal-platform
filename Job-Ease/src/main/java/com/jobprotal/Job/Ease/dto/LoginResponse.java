package com.jobprotal.Job.Ease.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String message;
}
