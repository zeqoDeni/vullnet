package org.vullnet.vullnet00.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.vullnet.vullnet00.model.BlogPost;

import java.util.Optional;

public interface BlogPostRepo extends JpaRepository<BlogPost, Long> {
    Page<BlogPost> findByPublishedTrueOrderByCreatedAtDesc(Pageable pageable);
    Page<BlogPost> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Optional<BlogPost> findBySlug(String slug);
}
