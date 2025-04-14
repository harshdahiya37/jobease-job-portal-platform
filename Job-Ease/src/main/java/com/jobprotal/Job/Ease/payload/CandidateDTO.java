package com.jobprotal.Job.Ease.payload;

import com.jobprotal.Job.Ease.entity.AppUser;
import com.jobprotal.Job.Ease.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean enabled;

    public static CandidateDTO fromEntity(AppUser user) {
        return new CandidateDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled()
        );
    }
}
