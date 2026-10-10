package com.campus.controller;

import com.campus.dto.Dtos.EventReq;
import com.campus.entity.*;
import com.campus.exception.ApiException;
import com.campus.repository.*;
import com.campus.service.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/events")
public class EventController {
    private final EventRepository events; private final RsvpRepository rsvps; private final CurrentUser me;
    public EventController(EventRepository events, RsvpRepository rsvps, CurrentUser me) { this.events = events; this.rsvps = rsvps; this.me = me; }

    @GetMapping public List<Event> list() { return events.findAllByOrderByStartsAtAsc(); }

    @PostMapping @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    public Event create(@Valid @RequestBody EventReq r) {
        var e = new Event();
        e.setTitle(r.title()); e.setDescription(r.description()); e.setVenue(r.venue()); e.setStartsAt(r.startsAt());
        return events.save(e);
    }

    @PostMapping("/{id}/rsvp") @Transactional
    public Event rsvp(@PathVariable Long id, Authentication a) {
        var u = me.get(a);
        var e = events.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found"));
        if (rsvps.existsByEventIdAndUserId(id, u.getId())) throw new ApiException(HttpStatus.CONFLICT, "You already RSVP'd");
        var r = new EventRsvp(); r.setEventId(id); r.setUserId(u.getId()); rsvps.save(r);
        e.setRsvpCount(e.getRsvpCount() + 1);
        return events.save(e);
    }
}
