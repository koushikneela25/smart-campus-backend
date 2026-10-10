package com.campus.controller;

import com.campus.dto.Dtos.*;
import com.campus.entity.*;
import com.campus.exception.ApiException;
import com.campus.repository.UserRepository;
import com.campus.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users; private final PasswordEncoder enc; private final JwtService jwt;
    public AuthController(UserRepository users, PasswordEncoder enc, JwtService jwt) { this.users = users; this.enc = enc; this.jwt = jwt; }

    @PostMapping("/register")
    public AuthRes register(@Valid @RequestBody RegisterReq r) {
        if (r.role() == Role.ADMIN) throw new ApiException(HttpStatus.FORBIDDEN, "Admin accounts are created by the college");
        if (users.existsByEmail(r.email())) throw new ApiException(HttpStatus.CONFLICT, "This email is already registered");
        var u = new AppUser();
        u.setName(r.name()); u.setEmail(r.email().toLowerCase()); u.setPassword(enc.encode(r.password()));
        u.setRole(r.role()); u.setDepartment(r.department());
        users.save(u);
        return new AuthRes(jwt.generate(u.getEmail(), u.getRole().name()), u.getName(), u.getEmail(), u.getRole());
    }

    @PostMapping("/login")
    public AuthRes login(@Valid @RequestBody LoginReq r) {
        var u = users.findByEmail(r.email().toLowerCase())
                .filter(x -> enc.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect"));
        return new AuthRes(jwt.generate(u.getEmail(), u.getRole().name()), u.getName(), u.getEmail(), u.getRole());
    }
}
