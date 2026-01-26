package org.vullnet.vullnet00.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPostResponse {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String coverImageUrl;
    private java.util.List<String> gallery;
    private String authorName;
    private boolean published;
    private Instant createdAt;
    private Instant updatedAt;
}
