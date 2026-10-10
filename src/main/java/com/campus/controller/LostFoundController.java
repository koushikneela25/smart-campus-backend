package com.campus.controller;

import com.campus.dto.Dtos.LostReq;
import com.campus.entity.LostItem;
import com.campus.exception.ApiException;
import com.campus.repository.LostItemRepository;
import com.campus.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/lostfound")
public class LostFoundController {
    private final LostItemRepository items; private final LostFoundService matcher; private final CurrentUser me;
    public LostFoundController(LostItemRepository items, LostFoundService matcher, CurrentUser me) { this.items = items; this.matcher = matcher; this.me = me; }

    @GetMapping public List<LostItem> list() { return items.findAllByOrderByCreatedAtDesc(); }

    @PostMapping
    public LostItem create(@Valid @RequestBody LostReq r, Authentication a) {
        var i = new LostItem();
        i.setKind(r.kind()); i.setTitle(r.title()); i.setDescription(r.description());
        i.setCategory(r.category()); i.setLocation(r.location()); i.setImageUrl(r.imageUrl());
        i.setReportedBy(me.get(a).getId());
        return items.save(i);
    }

    @GetMapping("/{id}/matches")
    public List<LostFoundService.Match> matches(@PathVariable Long id) {
        var base = items.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item not found"));
        return matcher.matches(base, items.findAll());
    }

    @PatchMapping("/{id}/resolve")
    public LostItem resolve(@PathVariable Long id, Authentication a) {
        var i = items.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item not found"));
        if (!i.getReportedBy().equals(me.get(a).getId())) throw new ApiException(HttpStatus.FORBIDDEN, "Only the reporter can resolve this");
        i.setResolved(true);
        return items.save(i);
    }
}
