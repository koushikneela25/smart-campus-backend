package com.campus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Getter @Setter @NoArgsConstructor
public class ClassSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String courseName;
    private Long facultyId;
    private Long zoneId;
    private boolean active = true;
    private Instant startedAt = Instant.now();
}
