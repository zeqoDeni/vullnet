package org.vullnet.vullnet00.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.vullnet.vullnet00.model.Review;

import java.util.Optional;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    Page<Review> findByRevieweeId(Long revieweeId, Pageable pageable);
    Optional<Review> findByHelpRequestIdAndReviewerId(Long helpRequestId, Long reviewerId);
    boolean existsByHelpRequestIdAndReviewerId(Long helpRequestId, Long reviewerId);
}
