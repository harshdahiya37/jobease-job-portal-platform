package com.jobprotal.Job.Ease.payload;

import lombok.Data;

@Data
public class JwtRequest {
    private String email;
    private String password;
}
