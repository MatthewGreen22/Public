package com.smartspeak.english_app.user.userfinder;

import com.smartspeak.english_app.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoDTO {
    private String username;
    private String email;
    private Role role;
}
