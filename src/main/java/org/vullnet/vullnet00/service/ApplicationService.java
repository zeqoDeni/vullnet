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
import org.vullnet.vullnet00.repo.UserRepo;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepo applicationRepo;
    private final UserRepo repo;
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

    @org.springframework.transaction.annotation.Transactional
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
        if (request.getStatus() == RequestStatus.CANCELLED || request.getStatus() == RequestStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request is closed");
        }
        app.setStatus(ApplicationStatus.ACCEPTED);
        app.setDecidedAt(LocalDateTime.now());
        app.setDecidedById(userId);
        request.setStatus(RequestStatus.IN_PROGRESS);
        request.setStatusUpdatedAt(LocalDateTime.now());
        applicationRepo.save(app);
        helpRequestRepo.save(request);
        return toResponse(app);
    }

    @org.springframework.transaction.annotation.Transactional
    public ApplicationResponse reject(Long userId, Long applicationId) {
        Application app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
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
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Application is not pending");
        }
        app.setStatus(ApplicationStatus.REJECTED);
        app.setDecidedAt(LocalDateTime.now());
        app.setDecidedById(userId);
        return toResponse(applicationRepo.save(app));
    }

    public org.springframework.data.domain.Page<ApplicationResponse> getByRequestId(Long requestId, org.springframework.data.domain.Pageable pageable) {
        if (!helpRequestRepo.existsById(requestId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found");
        }
        return applicationRepo.findByHelpRequestId(requestId, pageable).map(this::toResponse);
    }

    public org.springframework.data.domain.Page<ApplicationResponse> getByApplicantId(Long userId, org.springframework.data.domain.Pageable pageable) {
        return applicationRepo.findByApplicantId(userId, pageable).map(this::toResponse);
    }

    @org.springframework.transaction.annotation.Transactional
    public ApplicationResponse withdraw(Long userId, Long applicationId) {
        Application app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        if (app.getApplicant() == null || !app.getApplicant().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the applicant");
        }
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only pending applications can be withdrawn");
        }
        app.setStatus(ApplicationStatus.WITHDRAWN);
        app.setDecidedAt(LocalDateTime.now());
        app.setDecidedById(userId);
        return toResponse(applicationRepo.save(app));
    }

    private ApplicationResponse toResponse(Application app) {
        return ApplicationResponse.builder()
                .applicationId(app.getId())
                .helpRequestId(app.getHelpRequest() != null ? app.getHelpRequest().getId() : null)
                .applicantId(app.getApplicant() != null ? app.getApplicant().getId() : null)
                .build();
    }
}
