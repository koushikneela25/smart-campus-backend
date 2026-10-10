package com.campus.repository;

import com.campus.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface RsvpRepository extends JpaRepository<EventRsvp, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
}
