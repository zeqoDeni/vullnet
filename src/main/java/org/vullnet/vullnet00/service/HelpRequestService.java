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
import org.vullnet.vullnet00.repo.UserRepo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HelpRequestService {

    private final HelpRequestRepo helpRequestRepo;
    private final UserRepo userRepo;
    private final org.springframework.core.env.Environment environment;

    public HelpRequestResponse create(Long userId, HelpRequestCreateRequest req) {

        // TODO 1: load user or throw 404
        User owner = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int maxOpen = environment.getProperty("app.limits.max-open-requests", Integer.class, 10);
        long openCount = helpRequestRepo.countByOwnerIdAndStatus(owner.getId(), RequestStatus.OPEN);
        if (openCount >= maxOpen) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many open requests");
        }

        // TODO 2: build HelpRequest (set title/description/location/status/owner)
        HelpRequest helpRequest = HelpRequest.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .location(req.getLocation())
                .status(RequestStatus.OPEN)
                .statusUpdatedAt(LocalDateTime.now())
                .owner(owner)
                .createdBy(owner)
                .build();

        // TODO 3: save + return
        return toResponse(helpRequestRepo.save(helpRequest));
    }

    public Page<HelpRequestResponse> getAll(Long ownerId, RequestStatus status, Pageable pageable) {
        if (ownerId != null && status != null) {
            return helpRequestRepo.findByOwnerIdAndStatus(ownerId, status, pageable).map(this::toResponse);
        }
        if (ownerId != null) {
            return helpRequestRepo.findByOwnerId(ownerId, pageable).map(this::toResponse);
        }
        if (status != null) {
            return helpRequestRepo.findByStatus(status, pageable).map(this::toResponse);
        }
        return helpRequestRepo.findAll(pageable).map(this::toResponse);
    }

    public Page<HelpRequestResponse> getByOwnerId(Long ownerId, Pageable pageable) {
        return helpRequestRepo.findByOwnerId(ownerId, pageable).map(this::toResponse);
    }

    private HelpRequestResponse toResponse(HelpRequest request) {
        return HelpRequestResponse.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .status(request.getStatus() != null ? request.getStatus().name() : null)
                .ownerId(request.getOwner() != null ? request.getOwner().getId() : null)
                .build();
    }

    @org.springframework.transaction.annotation.Transactional
    public HelpRequestResponse complete(Long userId, Long requestId) {
        HelpRequest request = helpRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
        if (!request.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can complete");
        }
        if (request.getStatus() != RequestStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request is not in progress");
        }
        request.setStatus(RequestStatus.COMPLETED);
        request.setStatusUpdatedAt(LocalDateTime.now());
        return toResponse(helpRequestRepo.save(request));
    }

    @org.springframework.transaction.annotation.Transactional
    public HelpRequestResponse cancel(Long userId, Long requestId) {
        HelpRequest request = helpRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
        if (!request.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can cancel");
        }
        if (request.getStatus() == RequestStatus.COMPLETED || request.getStatus() == RequestStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request cannot be cancelled");
        }
        request.setStatus(RequestStatus.CANCELLED);
        request.setStatusUpdatedAt(LocalDateTime.now());
        return toResponse(helpRequestRepo.save(request));
    }
}
