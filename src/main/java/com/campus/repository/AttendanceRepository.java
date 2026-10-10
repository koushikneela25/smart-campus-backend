package com.campus.repository;

import com.campus.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    boolean existsBySessionIdAndStudentId(Long sessionId, Long studentId);
    List<AttendanceRecord> findByStudentIdOrderByMarkedAtDesc(Long studentId);
    List<AttendanceRecord> findBySessionId(Long sessionId);
}
