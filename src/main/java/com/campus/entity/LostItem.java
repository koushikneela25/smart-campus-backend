package com.campus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Getter @Setter @NoArgsConstructor
public class LostItem {
    public enum Kind { LOST, FOUND }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Enumerated(EnumType.STRING) private Kind kind;
    private String title;
    @Column(length = 1000) private String description;
    private String category;
    private String location;
    private String imageUrl;
    private Long reportedBy;
    private boolean resolved;
    private Instant createdAt = Instant.now();
}
