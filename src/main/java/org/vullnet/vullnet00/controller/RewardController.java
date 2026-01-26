package org.vullnet.vullnet00.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vullnet.vullnet00.dto.RewardResponse;
import org.vullnet.vullnet00.dto.UserResponse;
import org.vullnet.vullnet00.security.UserPrincipal;
import org.vullnet.vullnet00.service.RewardService;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @GetMapping("/me")
    public RewardResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return rewardService.getForUser(principal.getId());
    }

    @GetMapping("/leaderboard")
    public Page<UserResponse> leaderboard(Pageable pageable) {
        return rewardService.leaderboard(pageable);
    }
}
