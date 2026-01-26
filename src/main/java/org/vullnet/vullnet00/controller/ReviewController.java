package org.vullnet.vullnet00.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.vullnet.vullnet00.dto.ReviewCreateRequest;
import org.vullnet.vullnet00.dto.ReviewResponse;
import org.vullnet.vullnet00.security.UserPrincipal;
import org.vullnet.vullnet00.service.ReviewService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/requests/{id}/reviews")
    public ReviewResponse create(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long requestId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        return reviewService.create(principal.getId(), requestId, request);
    }

    @GetMapping("/users/{id}/reviews")
    public Page<ReviewResponse> reviewsForUser(
            @PathVariable("id") Long userId,
            Pageable pageable
    ) {
        return reviewService.getByUser(userId, pageable);
    }
}
