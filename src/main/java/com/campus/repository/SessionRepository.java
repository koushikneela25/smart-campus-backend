package com.campus.repository;

import com.campus.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface SessionRepository extends JpaRepository<ClassSession, Long> {
    List<ClassSession> findByFacultyIdOrderByStartedAtDesc(Long facultyId);
}
