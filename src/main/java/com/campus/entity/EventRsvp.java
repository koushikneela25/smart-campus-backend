package com.campus.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"eventId", "userId"}))
public class EventRsvp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long eventId;
    private Long userId;
}
