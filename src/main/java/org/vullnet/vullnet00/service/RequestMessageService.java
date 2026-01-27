package org.vullnet.vullnet00.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.vullnet.vullnet00.dto.RequestMessageRequest;
import org.vullnet.vullnet00.dto.RequestMessageResponse;
import org.vullnet.vullnet00.model.HelpRequest;
import org.vullnet.vullnet00.model.RequestMessage;
import org.vullnet.vullnet00.model.User;
import org.vullnet.vullnet00.repo.HelpRequestRepo;
import org.vullnet.vullnet00.repo.RequestMessageRepo;
import org.vullnet.vullnet00.repo.UserRepo;

@Service
@RequiredArgsConstructor
public class RequestMessageService {

    private final RequestMessageRepo requestMessageRepo;
    private final HelpRequestRepo helpRequestRepo;
    private final UserRepo userRepo;

    public RequestMessageResponse send(Long senderId, Long requestId, RequestMessageRequest req) {
        HelpRequest request = helpRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thirrja nuk u gjend"));
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Përdoruesi nuk u gjend"));

        // Only owner or accepted volunteer can chat
        if (!(request.getOwner() != null && request.getOwner().getId().equals(senderId))
                && !(request.getAcceptedVolunteer() != null && request.getAcceptedVolunteer().getId().equals(senderId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "S'ke akses në këtë bisedë");
        }

        RequestMessage msg = RequestMessage.builder()
                .helpRequest(request)
                .sender(sender)
                .body(req.getBody())
                .build();
        return toResponse(requestMessageRepo.save(msg));
    }

    public Page<RequestMessageResponse> list(Long viewerId, Long requestId, Pageable pageable) {
        HelpRequest request = helpRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thirrja nuk u gjend"));
        if (!(request.getOwner() != null && request.getOwner().getId().equals(viewerId))
                && !(request.getAcceptedVolunteer() != null && request.getAcceptedVolunteer().getId().equals(viewerId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "S'ke akses në këtë bisedë");
        }
        return requestMessageRepo.findByHelpRequestIdOrderByCreatedAtAsc(requestId, pageable).map(this::toResponse);
    }

    private RequestMessageResponse toResponse(RequestMessage msg) {
        return RequestMessageResponse.builder()
                .id(msg.getId())
                .senderId(msg.getSender() != null ? msg.getSender().getId() : null)
                .senderName(msg.getSender() != null ? msg.getSender().getFirstName() + " " + msg.getSender().getLastName() : null)
                .body(msg.getBody())
                .createdAt(msg.getCreatedAt())
                .build();
    }
}
