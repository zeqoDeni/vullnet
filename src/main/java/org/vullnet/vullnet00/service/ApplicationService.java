package org.vullnet.vullnet00.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.vullnet.vullnet00.dto.ApplicationCreateRequest;
import org.vullnet.vullnet00.dto.ApplicationResponse;
import org.vullnet.vullnet00.model.*;
import org.vullnet.vullnet00.repo.ApplicationRepo;
import org.vullnet.vullnet00.repo.HelpRequestRepo;
import org.vullnet.vullnet00.repo.Repo;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepo applicationRepo;
    private final Repo repo;
    private final HelpRequestRepo helpRequestRepo;


    public ApplicationResponse apply(Long userId, Long requestId, ApplicationCreateRequest req) {
        User applicant = repo.findById(userId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Useri nuk u gjend"));
        HelpRequest helpRequest = helpRequestRepo.findById(requestId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kjo thirrje per vullnetar nuk u gjend"));

        if (helpRequest.getStatus() != RequestStatus.OPEN) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Thirrja nuk eshte hapur");
        }

        Long ownerId = helpRequest.getOwner().getId();

        if(ownerId.equals(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Juve nuk mund te aplikoni ne thirrjen tuaj");
        }

        if (applicationRepo.existsByHelpRequestIdAndApplicantId(requestId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You already applied to this request");
        }

        Application app = Application.builder().helpRequest(helpRequest).applicant(applicant).status(ApplicationStatus.PENDING).message(req.getMessage()).build();
        return toResponse(applicationRepo.save(app));
    }

    public ApplicationResponse accept(Long userId, Long applicationId) {
        Application app = applicationRepo.findById(applicationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Application is not pending");
        }
        HelpRequest request = app.getHelpRequest();
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application has no request");
        }
        if (!request.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this request");
        }
        if (request.getStatus() != RequestStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request is not open");
        }
        app.setStatus(ApplicationStatus.ACCEPTED);
        request.setStatus(RequestStatus.IN_PROGRESS);
        applicationRepo.save(app);
        helpRequestRepo.save(request);
        return toResponse(app);
    }

    private ApplicationResponse toResponse(Application app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .helpRequestId(app.getHelpRequest() != null ? app.getHelpRequest().getId() : null)
                .applicantId(app.getApplicant() != null ? app.getApplicant().getId() : null)
                .message(app.getMessage())
                .status(app.getStatus() != null ? app.getStatus().name() : null)
                .createdAt(app.getCreatedAt())
                .build();
    }
}
