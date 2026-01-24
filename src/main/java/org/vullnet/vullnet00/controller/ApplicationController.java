package org.vullnet.vullnet00.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vullnet.vullnet00.dto.ApplicationCreateRequest;
import org.vullnet.vullnet00.dto.ApplicationResponse;
import org.vullnet.vullnet00.security.UserPrincipal;
import org.vullnet.vullnet00.service.ApplicationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/requests/{id}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse apply(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long requestId,
            @Valid @RequestBody ApplicationCreateRequest req
    ) {
        return applicationService.apply(principal.getId(), requestId, req);
    }

    @PatchMapping("/applications/{id}/accept")
    public ApplicationResponse accept(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long applicationId
    ) {
        return applicationService.accept(principal.getId(), applicationId);
    }

    @PatchMapping("/applications/{id}/reject")
    public ApplicationResponse reject(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long applicationId
    ) {
        return applicationService.reject(principal.getId(), applicationId);
    }

    @PatchMapping("/applications/{id}/withdraw")
    public ApplicationResponse withdraw(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long applicationId
    ) {
        return applicationService.withdraw(principal.getId(), applicationId);
    }

    @GetMapping("/applications")
    public Page<ApplicationResponse> getMyApplications(
            @AuthenticationPrincipal UserPrincipal principal,
            Pageable pageable
    ) {
        return applicationService.getByApplicantId(principal.getId(), pageable);
    }
}
