package com.campus.service;

import com.campus.entity.AppUser;
import com.campus.exception.ApiException;
import com.campus.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUser {
    private final UserRepository users;
    public CurrentUser(UserRepository users) { this.users = users; }
    public AppUser get(Authentication auth) {
        return users.findByEmail(auth.getName()).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
