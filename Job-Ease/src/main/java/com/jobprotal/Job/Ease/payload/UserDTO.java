package com.jobprotal.Job.Ease.payload;

import com.jobprotal.Job.Ease.entity.AppUser;
import com.jobprotal.Job.Ease.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean enabled;

    public static UserDTO fromEntity(AppUser user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled()
        );
    }
}
