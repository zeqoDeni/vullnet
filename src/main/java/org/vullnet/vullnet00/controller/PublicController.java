package org.vullnet.vullnet00.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vullnet.vullnet00.dto.PublicStatsResponse;
import org.vullnet.vullnet00.model.RequestStatus;
import org.vullnet.vullnet00.model.Role;
import org.vullnet.vullnet00.repo.HelpRequestRepo;
import org.vullnet.vullnet00.repo.UserRepo;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final HelpRequestRepo helpRequestRepo;
    private final UserRepo userRepo;

    @GetMapping("/stats")
    public PublicStatsResponse stats() {
        long openRequests = helpRequestRepo.countByStatus(RequestStatus.OPEN);
        long completedRequests = helpRequestRepo.countByStatus(RequestStatus.COMPLETED);
        long activeVolunteers = userRepo.countByRoleAndActiveTrue(Role.USER);
        return PublicStatsResponse.builder()
                .openRequests(openRequests)
                .activeVolunteers(activeVolunteers)
                .completedRequests(completedRequests)
                .build();
    }
}
