package org.vullnet.vullnet00.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.vullnet.vullnet00.dto.RequestMessageRequest;
import org.vullnet.vullnet00.dto.RequestMessageResponse;
import org.vullnet.vullnet00.security.UserPrincipal;
import org.vullnet.vullnet00.service.RequestMessageService;

@RestController
@RequestMapping("/api/requests/{id}/messages")
@RequiredArgsConstructor
public class RequestMessageController {

    private final RequestMessageService requestMessageService;

    @PostMapping
    public RequestMessageResponse send(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long requestId,
            @Valid @RequestBody RequestMessageRequest request
    ) {
        return requestMessageService.send(principal.getId(), requestId, request);
    }

    @GetMapping
    public Page<RequestMessageResponse> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long requestId,
            Pageable pageable
    ) {
        return requestMessageService.list(principal.getId(), requestId, pageable);
    }
}
