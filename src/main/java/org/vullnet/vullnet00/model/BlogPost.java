package org.vullnet.vullnet00.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "blog_posts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"slug"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 200)
    private String slug;

    @Column(length = 300)
    private String summary;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(length = 2048)
    private String coverImageUrl;

    @Column(columnDefinition = "TEXT")
    private String galleryImages; // newline-separated list

    @Column(length = 120)
    private String authorName;

    @Column(nullable = false)
    @Builder.Default
    private boolean published = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
