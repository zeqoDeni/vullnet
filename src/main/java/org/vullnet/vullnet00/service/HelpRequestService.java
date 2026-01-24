package org.vullnet.vullnet00.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.vullnet.vullnet00.dto.HelpRequestCreateRequest;
import org.vullnet.vullnet00.dto.HelpRequestResponse;
import org.vullnet.vullnet00.model.HelpRequest;
import org.vullnet.vullnet00.model.RequestStatus;
import org.vullnet.vullnet00.model.User;
import org.vullnet.vullnet00.repo.HelpRequestRepo;
import org.vullnet.vullnet00.repo.Repo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HelpRequestService {

    private final HelpRequestRepo helpRequestRepo;
    private final Repo userRepo;

    public HelpRequestResponse create(Long userId, HelpRequestCreateRequest req) {

        // TODO 1: load user or throw 404
        User owner = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // TODO 2: build HelpRequest (set title/description/location/status/owner)
        HelpRequest helpRequest = HelpRequest.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .status(RequestStatus.OPEN)
                .owner(owner)
                .build();

        // TODO 3: save + return
        return toResponse(helpRequestRepo.save(helpRequest));
    }

    public List<HelpRequestResponse> getAll() {
        return helpRequestRepo.findAll().stream().map(this::toResponse).toList();
    }

    private HelpRequestResponse toResponse(HelpRequest request) {
        return HelpRequestResponse.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus().name() : null)
                .ownerId(request.getOwner() != null ? request.getOwner().getId() : null)
                .createdById(request.getCreatedBy() != null ? request.getCreatedBy().getId() : null)
                .build();
    }
}
