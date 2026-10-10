package com.campus.service;

import com.campus.dto.Dtos.*;
import com.campus.entity.*;
import com.campus.exception.ApiException;
import com.campus.repository.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.*;

@Service
public class AttendanceService {
    public static final int QR_TTL_SECONDS = 30;
    private final StringRedisTemplate redis;
    private final SessionRepository sessions;
    private final ZoneRepository zones;
    private final AttendanceRepository attendance;

    public AttendanceService(StringRedisTemplate redis, SessionRepository sessions, ZoneRepository zones, AttendanceRepository attendance) {
        this.redis = redis; this.sessions = sessions; this.zones = zones; this.attendance = attendance;
    }

    public ClassSession start(AppUser faculty, StartSessionReq req) {
        zones.findById(req.zoneId()).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Unknown zone"));
        var s = new ClassSession();
        s.setCourseName(req.courseName()); s.setFacultyId(faculty.getId()); s.setZoneId(req.zoneId());
        return sessions.save(s);
    }

    /** Rotating QR token: one live token per session, expires after 30 seconds. */
    public Map<String, Object> issueToken(AppUser faculty, Long sessionId) {
        var s = ownedSession(faculty, sessionId);
        if (!s.isActive()) throw new ApiException(HttpStatus.BAD_REQUEST, "Session has ended");
        String token = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        redis.opsForValue().set("qr:" + sessionId, token, Duration.ofSeconds(QR_TTL_SECONDS));
        return Map.of("sessionId", sessionId, "token", token, "expiresInSeconds", QR_TTL_SECONDS);
    }

    public AttendanceRecord mark(AppUser student, MarkReq req) {
        var s = sessions.findById(req.sessionId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Session not found"));
        if (!s.isActive()) throw new ApiException(HttpStatus.BAD_REQUEST, "Session has ended");
        String live = redis.opsForValue().get("qr:" + s.getId());
        if (live == null || !live.equalsIgnoreCase(req.token().trim()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "Code expired or wrong. Ask for the current code.");
        var zone = zones.findById(s.getZoneId()).orElseThrow();
        double d = haversineMeters(req.lat(), req.lng(), zone.getLatitude(), zone.getLongitude());
        if (d > zone.getRadiusMeters())
            throw new ApiException(HttpStatus.FORBIDDEN, "You are " + Math.round(d) + " m from " + zone.getName() + ". Move inside the zone.");
        if (attendance.existsBySessionIdAndStudentId(s.getId(), student.getId()))
            throw new ApiException(HttpStatus.CONFLICT, "Attendance already marked");
        var r = new AttendanceRecord();
        r.setSessionId(s.getId()); r.setStudentId(student.getId());
        r.setStudentName(student.getName()); r.setCourseName(s.getCourseName());
        return attendance.save(r);
    }

    public ClassSession end(AppUser faculty, Long sessionId) {
        var s = ownedSession(faculty, sessionId);
        s.setActive(false);
        redis.delete("qr:" + sessionId);
        return sessions.save(s);
    }

    private ClassSession ownedSession(AppUser faculty, Long id) {
        var s = sessions.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Session not found"));
        if (!s.getFacultyId().equals(faculty.getId()) && faculty.getRole() != Role.ADMIN)
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your session");
        return s;
    }

    static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000, p1 = Math.toRadians(lat1), p2 = Math.toRadians(lat2);
        double dp = Math.toRadians(lat2 - lat1), dl = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dp / 2) * Math.sin(dp / 2) + Math.cos(p1) * Math.cos(p2) * Math.sin(dl / 2) * Math.sin(dl / 2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
