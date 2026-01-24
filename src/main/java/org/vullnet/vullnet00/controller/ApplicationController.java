package org.vullnet.vullnet00.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vullnet.vullnet00.dto.ApplicationCreateRequest;
import org.vullnet.vullnet00.model.Application;
import org.vullnet.vullnet00.service.ApplicationService;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/{id}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public Application apply(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable("id") Long requestId,
            @Valid @RequestBody ApplicationCreateRequest req
    ) {
        return applicationService.apply(userId, requestId, req);
    }
}
