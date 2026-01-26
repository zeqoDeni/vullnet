package org.vullnet.vullnet00.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.vullnet.vullnet00.dto.BlogPostRequest;
import org.vullnet.vullnet00.dto.BlogPostResponse;
import org.vullnet.vullnet00.security.UserPrincipal;
import org.vullnet.vullnet00.service.BlogService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping("/blogs")
    public Page<BlogPostResponse> published(Pageable pageable) {
        return blogService.getPublished(pageable);
    }

    @GetMapping("/blogs/{slug}")
    public BlogPostResponse one(@PathVariable("slug") String slug) {
        try {
            Long id = Long.parseLong(slug);
            return blogService.getById(id);
        } catch (NumberFormatException ex) {
            return blogService.getBySlug(slug);
        }
    }

    @GetMapping("/admin/blogs")
    public Page<BlogPostResponse> adminList(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable
    ) {
        enforceAdmin(principal);
        return blogService.getAll(pageable);
    }

    @PostMapping("/admin/blogs")
    public BlogPostResponse create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody BlogPostRequest request
    ) {
        enforceAdmin(principal);
        return blogService.create(principal.getId(), request);
    }

    @PutMapping("/admin/blogs/{id}")
    public BlogPostResponse update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long id,
            @Valid @RequestBody BlogPostRequest request
    ) {
        enforceAdmin(principal);
        return blogService.update(id, request);
    }

    @DeleteMapping("/admin/blogs/{id}")
    public void delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long id
    ) {
        enforceAdmin(principal);
        blogService.delete(id);
    }

    private void enforceAdmin(UserPrincipal principal) {
        if (principal == null || principal.getAuthorities().stream().noneMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()))) {
            throw new AccessDeniedException("Kërkohet akses administratori");
        }
    }
}
