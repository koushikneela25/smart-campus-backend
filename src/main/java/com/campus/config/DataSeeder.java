package com.campus.config;

import com.campus.entity.*;
import com.campus.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/** Demo data. Admin login: admin@campus.edu / admin123 (change before deploying). Edit zone coordinates to your campus. */
@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users; private final ZoneRepository zones; private final EventRepository events; private final PasswordEncoder enc;
    public DataSeeder(UserRepository users, ZoneRepository zones, EventRepository events, PasswordEncoder enc) {
        this.users = users; this.zones = zones; this.events = events; this.enc = enc;
    }
    @Override public void run(String... args) {
        if (!users.existsByEmail("admin@campus.edu")) {
            var a = new AppUser(); a.setName("Campus Admin"); a.setEmail("admin@campus.edu");
            a.setPassword(enc.encode("admin123")); a.setRole(Role.ADMIN); users.save(a);
        }
        if (zones.count() == 0) {
            var z = new GeofenceZone(); z.setName("Main Block"); z.setLatitude(17.3850); z.setLongitude(78.4867); z.setRadiusMeters(150);
            zones.save(z);
        }
        if (events.count() == 0) {
            var e = new Event(); e.setTitle("Hackathon Kickoff"); e.setDescription("Teams form, problems drop, pizza arrives.");
            e.setVenue("Auditorium"); e.setStartsAt(LocalDateTime.now().plusDays(3)); events.save(e);
        }
    }
}
