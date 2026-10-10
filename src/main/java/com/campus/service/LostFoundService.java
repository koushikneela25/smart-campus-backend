package com.campus.service;

import com.campus.entity.LostItem;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.*;

/** Simple matcher: category, location and word overlap. Swap in image similarity later. */
@Service
public class LostFoundService {
    public record Match(LostItem item, int score) {}

    public List<Match> matches(LostItem base, List<LostItem> all) {
        var opposite = base.getKind() == LostItem.Kind.LOST ? LostItem.Kind.FOUND : LostItem.Kind.LOST;
        return all.stream()
                .filter(i -> i.getKind() == opposite && !i.isResolved())
                .map(i -> new Match(i, score(base, i)))
                .filter(m -> m.score() >= 25)
                .sorted(Comparator.comparingInt(Match::score).reversed())
                .limit(5).collect(Collectors.toList());
    }

    private int score(LostItem a, LostItem b) {
        int s = 0;
        if (eq(a.getCategory(), b.getCategory())) s += 35;
        if (eq(a.getLocation(), b.getLocation())) s += 25;
        var wa = words(a.getTitle() + " " + a.getDescription());
        var wb = words(b.getTitle() + " " + b.getDescription());
        if (!wa.isEmpty() && !wb.isEmpty()) {
            var inter = new HashSet<>(wa); inter.retainAll(wb);
            var union = new HashSet<>(wa); union.addAll(wb);
            s += (int) Math.round(40.0 * inter.size() / union.size());
        }
        return Math.min(s, 100);
    }
    private boolean eq(String x, String y) { return x != null && y != null && x.equalsIgnoreCase(y); }
    private Set<String> words(String t) {
        if (t == null) return Set.of();
        return Arrays.stream(t.toLowerCase().split("\\W+")).filter(w -> w.length() > 2).collect(Collectors.toSet());
    }
}
