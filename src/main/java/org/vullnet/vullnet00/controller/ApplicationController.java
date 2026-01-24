package org.vullnet.vullnet00.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vullnet.vullnet00.dto.ApplicationCreateRequest;
import org.vullnet.vullnet00.dto.ApplicationResponse;
import org.vullnet.vullnet00.service.ApplicationService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/requests/{id}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse apply(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable("id") Long requestId,
            @Valid @RequestBody ApplicationCreateRequest req
    ) {
        return applicationService.apply(userId, requestId, req);
    }

    @PatchMapping("/applications/{id}/accept")
    public ApplicationResponse accept(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable("id") Long applicationId
    ) {
        return applicationService.accept(userId, applicationId);
    }
}
