package com.smartspeak.english_app.user.userfinder;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserFinder {
    UserDetails findByToken(String token);
}
