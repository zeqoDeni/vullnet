package org.vullnet.vullnet00.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vullnet.vullnet00.dto.AdminStatsResponse;
import org.vullnet.vullnet00.model.RequestStatus;
import org.vullnet.vullnet00.repo.ApplicationRepo;
import org.vullnet.vullnet00.repo.HelpRequestRepo;
import org.vullnet.vullnet00.repo.UserRepo;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepo userRepo;
    private final HelpRequestRepo helpRequestRepo;
    private final ApplicationRepo applicationRepo;

    public AdminStatsResponse getStats() {
        return AdminStatsResponse.builder()
                .users(userRepo.count())
                .requests(helpRequestRepo.count())
                .openRequests(helpRequestRepo.countByStatus(RequestStatus.OPEN))
                .applications(applicationRepo.count())
                .build();
    }
}
