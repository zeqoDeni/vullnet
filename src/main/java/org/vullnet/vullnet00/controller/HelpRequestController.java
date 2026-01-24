package org.vullnet.vullnet00.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.vullnet.vullnet00.dto.HelpRequestCreateRequest;
import org.vullnet.vullnet00.dto.HelpRequestResponse;
import org.vullnet.vullnet00.service.HelpRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class HelpRequestController {

    private final HelpRequestService helpRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HelpRequestResponse create(
            @RequestHeader("X-USER-ID") Long userId,
            @Valid @RequestBody HelpRequestCreateRequest request
    ) {
        return helpRequestService.create(userId, request);
    }

    @GetMapping
    public List<HelpRequestResponse> getAll() {
        return helpRequestService.getAll();
    }
}
