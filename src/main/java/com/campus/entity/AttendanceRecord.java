package com.campus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Getter @Setter @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"sessionId", "studentId"}))
public class AttendanceRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long sessionId;
    private Long studentId;
    private String studentName;
    private String courseName;
    private Instant markedAt = Instant.now();
}
