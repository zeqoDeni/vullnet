package org.vullnet.vullnet00.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vullnet.vullnet00.dto.ApplicationResponse;
import org.vullnet.vullnet00.dto.HelpRequestCreateRequest;
import org.vullnet.vullnet00.dto.HelpRequestResponse;
import org.vullnet.vullnet00.model.RequestStatus;
import org.vullnet.vullnet00.service.ApplicationService;
import org.vullnet.vullnet00.service.HelpRequestService;
import org.vullnet.vullnet00.security.UserPrincipal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class HelpRequestController {

    private final HelpRequestService helpRequestService;
    private final ApplicationService applicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HelpRequestResponse create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody HelpRequestCreateRequest request
    ) {
        return helpRequestService.create(principal.getId(), request);
    }

    @GetMapping
    public Page<HelpRequestResponse> getAll(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "ownerId", required = false) Long ownerId,
            @RequestParam(value = "status", required = false) RequestStatus status,
            @RequestParam(value = "q", required = false) String search,
            Pageable pageable
    ) {
        Long viewerId = principal != null ? principal.getId() : null;
        return helpRequestService.getAll(ownerId, status, search, pageable, viewerId);
    }

    @GetMapping("/{id:\\d+}")
    public HelpRequestResponse getOne(@AuthenticationPrincipal UserPrincipal principal, @PathVariable("id") Long id) {
        Long viewerId = principal != null ? principal.getId() : null;
        return helpRequestService.getOne(id, viewerId);
    }

    @GetMapping("/open")
    public Page<HelpRequestResponse> getOpenRequests(Pageable pageable) {
        return helpRequestService.getAll(null, RequestStatus.OPEN, null, pageable, null);
    }

    @GetMapping("/{id}/applications")
    public Page<ApplicationResponse> getApplications(
            @PathVariable("id") Long requestId,
            Pageable pageable
    ) {
        return applicationService.getByRequestId(requestId, pageable);
    }

    @PatchMapping("/{id}/complete")
    public HelpRequestResponse complete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long requestId
    ) {
        return helpRequestService.complete(principal.getId(), requestId);
    }

    @PatchMapping("/{id}/cancel")
    public HelpRequestResponse cancel(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long requestId
    ) {
        return helpRequestService.cancel(principal.getId(), requestId);
    }
}
