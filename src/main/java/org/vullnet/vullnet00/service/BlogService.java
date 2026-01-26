package org.vullnet.vullnet00.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.vullnet.vullnet00.dto.BlogPostRequest;
import org.vullnet.vullnet00.dto.BlogPostResponse;
import org.vullnet.vullnet00.model.BlogPost;
import org.vullnet.vullnet00.model.User;
import org.vullnet.vullnet00.repo.BlogPostRepo;
import org.vullnet.vullnet00.repo.UserRepo;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogPostRepo blogPostRepo;
    private final UserRepo userRepo;

    public Page<BlogPostResponse> getPublished(Pageable pageable) {
        return blogPostRepo.findByPublishedTrueOrderByCreatedAtDesc(pageable).map(this::toResponse);
    }

    public Page<BlogPostResponse> getAll(Pageable pageable) {
        return blogPostRepo.findAllByOrderByCreatedAtDesc(pageable).map(this::toResponse);
    }

    public BlogPostResponse getById(Long id) {
        return blogPostRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artikulli nuk u gjet"));
    }

    public BlogPostResponse getBySlug(String slug) {
        return blogPostRepo.findBySlug(slug).map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artikulli nuk u gjet"));
    }

    public BlogPostResponse create(Long authorId, BlogPostRequest request) {
        User author = authorId != null ? userRepo.findById(authorId).orElse(null) : null;
        String authorName = author != null ? author.getFirstName() + " " + author.getLastName() : "Stafi Vullnet";
        String slug = buildUniqueSlug(request.getTitle(), null);

        BlogPost post = BlogPost.builder()
                .title(request.getTitle())
                .slug(slug)
                .summary(request.getSummary())
                .content(request.getContent())
                .coverImageUrl(request.getCoverImageUrl())
                .galleryImages(serializeGallery(request.getGallery()))
                .authorName(authorName)
                .published(Boolean.TRUE.equals(request.getPublished()))
                .build();
        return toResponse(blogPostRepo.save(post));
    }

    public BlogPostResponse update(Long postId, BlogPostRequest request) {
        BlogPost existing = blogPostRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artikulli nuk u gjet"));
        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
            existing.setSlug(buildUniqueSlug(request.getTitle(), existing.getId()));
        }
        if (request.getSummary() != null) {
            existing.setSummary(request.getSummary());
        }
        if (request.getContent() != null) {
            existing.setContent(request.getContent());
        }
        if (request.getCoverImageUrl() != null) {
            existing.setCoverImageUrl(request.getCoverImageUrl());
        }
        if (request.getGallery() != null) {
            existing.setGalleryImages(serializeGallery(request.getGallery()));
        }
        if (request.getPublished() != null) {
            existing.setPublished(request.getPublished());
        }
        return toResponse(blogPostRepo.save(existing));
    }

    public void delete(Long postId) {
        if (!blogPostRepo.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artikulli nuk u gjet");
        }
        blogPostRepo.deleteById(postId);
    }

    private String buildUniqueSlug(String title, Long currentId) {
        String base = slugify(title);
        String candidate = base;
        int i = 1;
        Optional<BlogPost> existing = blogPostRepo.findBySlug(candidate);
        while (existing.isPresent() && !existing.get().getId().equals(currentId)) {
            candidate = base + "-" + i;
            existing = blogPostRepo.findBySlug(candidate);
            i++;
        }
        return candidate;
    }

    private String slugify(String input) {
        if (input == null) return "artikull";
        String nowhitespace = input.trim().replaceAll("[\\s]+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[^\\w-]", "").toLowerCase(Locale.ROOT);
        return slug.isEmpty() ? "artikull" : slug;
    }

    private BlogPostResponse toResponse(BlogPost post) {
        return BlogPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .summary(post.getSummary())
                .content(post.getContent())
                .coverImageUrl(post.getCoverImageUrl())
                .gallery(deserializeGallery(post.getGalleryImages()))
                .authorName(post.getAuthorName())
                .published(post.isPublished())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private String serializeGallery(java.util.List<String> gallery) {
        if (gallery == null) return null;
        return gallery.stream()
                .filter(url -> url != null && !url.isBlank())
                .map(String::trim)
                .collect(java.util.stream.Collectors.joining("\n"));
    }

    private java.util.List<String> deserializeGallery(String data) {
        if (data == null || data.isBlank()) return java.util.Collections.emptyList();
        return java.util.Arrays.stream(data.split("\\n"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
