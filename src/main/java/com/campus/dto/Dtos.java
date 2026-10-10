package com.campus.dto;

import com.campus.entity.Role;
import jakarta.validation.constraints.*;

public class Dtos {
    public record RegisterReq(@NotBlank String name, @Email @NotBlank String email,
                              @Size(min = 6) String password, @NotNull Role role, String department) {}
    public record LoginReq(@Email @NotBlank String email, @NotBlank String password) {}
    public record AuthRes(String token, String name, String email, Role role) {}
    public record StartSessionReq(@NotBlank String courseName, @NotNull Long zoneId) {}
    public record MarkReq(@NotNull Long sessionId, @NotBlank String token, double lat, double lng) {}
    public record EventReq(@NotBlank String title, String description, String venue,
                           @NotNull java.time.LocalDateTime startsAt) {}
    public record LostReq(@NotNull com.campus.entity.LostItem.Kind kind, @NotBlank String title,
                          String description, String category, String location, String imageUrl) {}
}
