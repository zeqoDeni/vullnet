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
    private final RewardService rewardService;
    private final NotificationService notificationService;

    public HelpRequestResponse create(Long userId, HelpRequestCreateRequest req) {

        User owner = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Përdoruesi nuk u gjend"));

        int maxOpen = environment.getProperty("app.limits.max-open-requests", Integer.class, 10);
        long openCount = helpRequestRepo.countByOwnerIdAndStatus(owner.getId(), RequestStatus.OPEN);
        if (openCount >= maxOpen) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Ke arritur kufirin e thirrjeve të hapura");
        }

        HelpRequest helpRequest = HelpRequest.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .location(req.getLocation())
                .status(RequestStatus.OPEN)
                .statusUpdatedAt(LocalDateTime.now())
                .owner(owner)
                .createdBy(owner)
                .acceptedVolunteer(null)
                .acceptedApplicationId(null)
                .imageUrl(req.getImageUrl())
                .build();

        return toResponse(helpRequestRepo.save(helpRequest), userId);
    }

    public Page<HelpRequestResponse> getAll(Long ownerId, RequestStatus status, String search, Pageable pageable, Long viewerId) {
        if (search != null && !search.isBlank()) {
            String q = search.trim();
            return helpRequestRepo.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrLocationContainingIgnoreCase(q, q, q, pageable)
                    .map(req -> toResponse(req, viewerId));
        }
        if (ownerId != null && status != null) {
            return helpRequestRepo.findByOwnerIdAndStatus(ownerId, status, pageable).map(req -> toResponse(req, viewerId));
        }
        if (ownerId != null) {
            return helpRequestRepo.findByOwnerId(ownerId, pageable).map(req -> toResponse(req, viewerId));
        }
        if (status != null) {
            return helpRequestRepo.findByStatus(status, pageable).map(req -> toResponse(req, viewerId));
        }
        return helpRequestRepo.findAll(pageable).map(req -> toResponse(req, viewerId));
    }

    public Page<HelpRequestResponse> getByOwnerId(Long ownerId, Pageable pageable, Long viewerId) {
        return helpRequestRepo.findByOwnerId(ownerId, pageable).map(req -> toResponse(req, viewerId));
    }

    private HelpRequestResponse toResponse(HelpRequest request, Long viewerId) {
        boolean canSeeContact = viewerId != null && (
                (request.getOwner() != null && viewerId.equals(request.getOwner().getId())) ||
                        (request.getAcceptedVolunteer() != null && viewerId.equals(request.getAcceptedVolunteer().getId()))
        );
        return HelpRequestResponse.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .status(request.getStatus() != null ? request.getStatus().name() : null)
                .ownerId(request.getOwner() != null ? request.getOwner().getId() : null)
                .ownerName(request.getOwner() != null ? (request.getOwner().getFirstName() + " " + request.getOwner().getLastName()) : null)
                .ownerAvatar(request.getOwner() != null ? request.getOwner().getAvatarUrl() : null)
                .ownerPhone(canSeeContact && request.getOwner() != null ? request.getOwner().getPhone() : null)
                .acceptedVolunteerId(request.getAcceptedVolunteer() != null ? request.getAcceptedVolunteer().getId() : null)
                .acceptedVolunteerName(request.getAcceptedVolunteer() != null ? request.getAcceptedVolunteer().getFirstName() + " " + request.getAcceptedVolunteer().getLastName() : null)
                .acceptedVolunteerPhone(canSeeContact && request.getAcceptedVolunteer() != null ? request.getAcceptedVolunteer().getPhone() : null)
                .completedAt(request.getCompletedAt())
                .imageUrl(request.getImageUrl())
                .build();
    }

    @org.springframework.transaction.annotation.Transactional
    public HelpRequestResponse complete(Long userId, Long requestId) {
        HelpRequest request = helpRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thirrja nuk u gjend"));
        if (!request.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vetëm pronari mund ta përfundojë");
        }
        if (request.getStatus() != RequestStatus.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Thirrja nuk është në progres");
        }
        if (request.getAcceptedVolunteer() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nuk ka vullnetar të pranuar për këtë thirrje");
        }
        request.setStatus(RequestStatus.COMPLETED);
        request.setStatusUpdatedAt(LocalDateTime.now());
        request.setCompletedAt(LocalDateTime.now());
        HelpRequest saved = helpRequestRepo.save(request);
        rewardService.awardForCompletion(saved.getAcceptedVolunteer());
        notificationService.notifyEmail(saved.getOwner(), "Thirrja u përfundua", "Thirrja \"" + saved.getTitle() + "\" u shënua si e përfunduar.");
        notificationService.notifyEmail(saved.getAcceptedVolunteer(), "Thirrja u përfundua", "Thirrja \"" + saved.getTitle() + "\" u përfundua.");
        return toResponse(saved, userId);
    }

    @org.springframework.transaction.annotation.Transactional
    public HelpRequestResponse cancel(Long userId, Long requestId) {
        HelpRequest request = helpRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thirrja nuk u gjend"));
        if (!request.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vetëm pronari mund ta anulojë");
        }
        if (request.getStatus() == RequestStatus.COMPLETED || request.getStatus() == RequestStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Thirrja nuk mund të anulohet");
        }
        request.setStatus(RequestStatus.CANCELLED);
        request.setStatusUpdatedAt(LocalDateTime.now());
        HelpRequest saved = helpRequestRepo.save(request);
        if (saved.getAcceptedVolunteer() != null) {
            notificationService.notifyEmail(saved.getAcceptedVolunteer(), "Thirrja u anulua", "Thirrja \"" + saved.getTitle() + "\" u anulua.");
            notificationService.notifySms(saved.getAcceptedVolunteer(), "Thirrja \"" + saved.getTitle() + "\" u anulua.");
        }
        return toResponse(saved, userId);
    }

    public HelpRequestResponse getOne(Long id, Long viewerId) {
        HelpRequest request = helpRequestRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thirrja nuk u gjend"));
        return toResponse(request, viewerId);
    }
}
