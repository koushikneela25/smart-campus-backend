package com.campus.controller;

import com.campus.dto.Dtos.*;
import com.campus.entity.*;
import com.campus.repository.*;
import com.campus.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService svc; private final CurrentUser me;
    private final ZoneRepository zones; private final AttendanceRepository records; private final SessionRepository sessions;

    public AttendanceController(AttendanceService svc, CurrentUser me, ZoneRepository zones, AttendanceRepository records, SessionRepository sessions) {
        this.svc = svc; this.me = me; this.zones = zones; this.records = records; this.sessions = sessions;
    }

    @GetMapping("/zones") public List<GeofenceZone> zones() { return zones.findAll(); }

    @PostMapping("/sessions") @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    public ClassSession start(@Valid @RequestBody StartSessionReq r, Authentication a) { return svc.start(me.get(a), r); }

    @GetMapping("/sessions/mine") @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    public List<ClassSession> mine(Authentication a) { return sessions.findByFacultyIdOrderByStartedAtDesc(me.get(a).getId()); }

    @GetMapping("/sessions/{id}/token") @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    public Map<String, Object> token(@PathVariable Long id, Authentication a) { return svc.issueToken(me.get(a), id); }

    @GetMapping("/sessions/{id}/records") @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    public List<AttendanceRecord> sessionRecords(@PathVariable Long id) { return records.findBySessionId(id); }

    @PostMapping("/sessions/{id}/end") @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    public ClassSession end(@PathVariable Long id, Authentication a) { return svc.end(me.get(a), id); }

    @PostMapping("/mark") @PreAuthorize("hasRole('STUDENT')")
    public AttendanceRecord mark(@Valid @RequestBody MarkReq r, Authentication a) { return svc.mark(me.get(a), r); }

    @GetMapping("/me") @PreAuthorize("hasRole('STUDENT')")
    public List<AttendanceRecord> history(Authentication a) { return records.findByStudentIdOrderByMarkedAtDesc(me.get(a).getId()); }
}
