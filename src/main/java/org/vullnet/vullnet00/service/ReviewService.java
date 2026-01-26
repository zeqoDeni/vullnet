package org.vullnet.vullnet00.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.vullnet.vullnet00.dto.ReviewCreateRequest;
import org.vullnet.vullnet00.dto.ReviewResponse;
import org.vullnet.vullnet00.model.HelpRequest;
import org.vullnet.vullnet00.model.Review;
import org.vullnet.vullnet00.model.User;
import org.vullnet.vullnet00.repo.HelpRequestRepo;
import org.vullnet.vullnet00.repo.ReviewRepo;
import org.vullnet.vullnet00.repo.UserRepo;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepo reviewRepo;
    private final HelpRequestRepo helpRequestRepo;
    private final UserRepo userRepo;

    public ReviewResponse create(Long reviewerId, Long helpRequestId, ReviewCreateRequest req) {
        User reviewer = userRepo.findById(reviewerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Përdoruesi nuk u gjend"));
        User reviewee = userRepo.findById(req.getRevieweeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Përdoruesi për vlerësim nuk u gjend"));
        HelpRequest helpRequest = helpRequestRepo.findById(helpRequestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thirrja nuk u gjend"));

        if (helpRequest.getAcceptedVolunteer() == null || helpRequest.getStatus() != org.vullnet.vullnet00.model.RequestStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vlerësimet lejohen vetëm pasi thirrja të ketë përfunduar");
        }

        Long ownerId = helpRequest.getOwner().getId();
        Long acceptedVolunteerId = helpRequest.getAcceptedVolunteer().getId();

        boolean reviewerIsOwner = reviewer.getId().equals(ownerId);
        boolean reviewerIsVolunteer = reviewer.getId().equals(acceptedVolunteerId);
        boolean revieweeIsCounterpart = (reviewee.getId().equals(ownerId) && reviewerIsVolunteer)
                || (reviewee.getId().equals(acceptedVolunteerId) && reviewerIsOwner);

        if ((!reviewerIsOwner && !reviewerIsVolunteer) || !revieweeIsCounterpart) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Mund të vlerësohen vetëm pjesëmarrësit e thirrjes");
        }

        if (reviewRepo.existsByHelpRequestIdAndReviewerId(helpRequestId, reviewerId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ke lënë tashmë një vlerësim për këtë thirrje");
        }

        Review review = Review.builder()
                .helpRequest(helpRequest)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .rating(req.getRating())
                .comment(req.getComment())
                .build();

        Review saved = reviewRepo.save(review);

        // përditëso statistikat e reviewee
        int prevCount = reviewee.getReviewCount();
        double prevAvg = reviewee.getAverageRating();
        int newCount = prevCount + 1;
        double newAvg = ((prevAvg * prevCount) + req.getRating()) / newCount;
        reviewee.setReviewCount(newCount);
        reviewee.setAverageRating(newAvg);
        userRepo.save(reviewee);

        return toResponse(saved);
    }

    public Page<ReviewResponse> getByUser(Long userId, Pageable pageable) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Përdoruesi nuk u gjend");
        }
        return reviewRepo.findByRevieweeId(userId, pageable).map(this::toResponse);
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .helpRequestId(review.getHelpRequest() != null ? review.getHelpRequest().getId() : null)
                .reviewerId(review.getReviewer() != null ? review.getReviewer().getId() : null)
                .reviewerName(review.getReviewer() != null ? review.getReviewer().getFirstName() + " " + review.getReviewer().getLastName() : null)
                .revieweeId(review.getReviewee() != null ? review.getReviewee().getId() : null)
                .revieweeName(review.getReviewee() != null ? review.getReviewee().getFirstName() + " " + review.getReviewee().getLastName() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
