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
            @RequestParam(value = "ownerId", required = false) Long ownerId,
            @RequestParam(value = "status", required = false) RequestStatus status,
            Pageable pageable
    ) {
        return helpRequestService.getAll(ownerId, status, pageable);
    }

    @GetMapping("/open")
    public Page<HelpRequestResponse> getOpenRequests(Pageable pageable) {
        return helpRequestService.getAll(null, RequestStatus.OPEN, pageable);
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
