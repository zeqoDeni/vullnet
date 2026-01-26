package org.vullnet.vullnet00.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.vullnet.vullnet00.dto.RewardResponse;
import org.vullnet.vullnet00.dto.UserResponse;
import org.vullnet.vullnet00.model.User;
import org.vullnet.vullnet00.repo.UserRepo;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final UserRepo userRepo;
    private final Environment environment;

    public RewardResponse awardForCompletion(User volunteer) {
        if (volunteer == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vullnetari nuk u gjet");
        }
        int pointsPerCompletion = environment.getProperty("app.rewards.completion-points", Integer.class, 20);
        volunteer.setCompletedRequests(volunteer.getCompletedRequests() + 1);
        volunteer.setRewardPoints(volunteer.getRewardPoints() + pointsPerCompletion);
        User saved = userRepo.save(volunteer);
        return toReward(saved);
    }

    public RewardResponse getForUser(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Përdoruesi nuk u gjet"));
        return toReward(user);
    }

    public Page<UserResponse> leaderboard(Pageable pageable) {
        return userRepo.findAllByOrderByRewardPointsDesc(pageable).map(this::toUserResponse);
    }

    private RewardResponse toReward(User user) {
        return RewardResponse.builder()
                .points(user.getRewardPoints())
                .completedRequests(user.getCompletedRequests())
                .averageRating(user.getAverageRating())
                .reviewCount(user.getReviewCount())
                .build();
    }

    private UserResponse toUserResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .role(u.getRole() != null ? u.getRole().name() : null)
                .active(u.isActive())
                .rewardPoints(u.getRewardPoints())
                .completedRequests(u.getCompletedRequests())
                .averageRating(u.getAverageRating())
                .reviewCount(u.getReviewCount())
                .build();
    }
}
