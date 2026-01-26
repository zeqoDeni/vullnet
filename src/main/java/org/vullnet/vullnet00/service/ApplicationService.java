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
    private final NotificationService notificationService;


    public ApplicationResponse apply(Long userId, Long requestId, ApplicationCreateRequest req) {
        User applicant = repo.findById(userId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Përdoruesi nuk u gjend"));
        HelpRequest helpRequest = helpRequestRepo.findById(requestId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thirrja nuk u gjend"));

        if (helpRequest.getStatus() != RequestStatus.OPEN) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Thirrja nuk është e hapur");
        }

        Long ownerId = helpRequest.getOwner().getId();

        if(ownerId.equals(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nuk mund të aplikoni në thirrjen tuaj");
        }

        if (applicationRepo.existsByHelpRequestIdAndApplicantId(requestId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ke aplikuar tashmë për këtë thirrje");
        }

        Application app = Application.builder().helpRequest(helpRequest).applicant(applicant).status(ApplicationStatus.PENDING).message(req.getMessage()).build();
        notificationService.notifyEmail(helpRequest.getOwner(), "Aplikim i ri", "Ke një aplikim të ri për thirrjen: " + helpRequest.getTitle());
        notificationService.notifySms(helpRequest.getOwner(), "Aplikim i ri për \"" + helpRequest.getTitle() + "\"");
        return toResponse(applicationRepo.save(app));
    }

    @org.springframework.transaction.annotation.Transactional
    public ApplicationResponse accept(Long userId, Long applicationId) {
        Application app = applicationRepo.findById(applicationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aplikimi nuk u gjet"));
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Aplikimi nuk është në pritje");
        }
        HelpRequest request = app.getHelpRequest();
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aplikimi nuk lidhet me thirrje");
        }
        if (!request.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nuk je pronari i kësaj thirrjeje");
        }
        if (request.getStatus() != RequestStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Thirrja nuk është e hapur");
        }
        app.setStatus(ApplicationStatus.ACCEPTED);
        app.setDecidedAt(LocalDateTime.now());
        app.setDecidedById(userId);
        request.setStatus(RequestStatus.IN_PROGRESS);
        request.setStatusUpdatedAt(LocalDateTime.now());
        request.setAcceptedApplicationId(app.getId());
        request.setAcceptedVolunteer(app.getApplicant());
        applicationRepo.save(app);
        helpRequestRepo.save(request);
        notificationService.notifyEmail(app.getApplicant(), "Aplikimi u pranua", "Thirrja \"" + request.getTitle() + "\" ju pranoi.");
        notificationService.notifySms(app.getApplicant(), "Aplikimi u pranua për \"" + request.getTitle() + "\"");
        return toResponse(app);
    }

    @org.springframework.transaction.annotation.Transactional
    public ApplicationResponse reject(Long userId, Long applicationId) {
        Application app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aplikimi nuk u gjet"));
        HelpRequest request = app.getHelpRequest();
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aplikimi nuk lidhet me thirrje");
        }
        if (!request.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nuk je pronari i kësaj thirrjeje");
        }
        if (request.getStatus() != RequestStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Thirrja nuk është e hapur");
        }
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Aplikimi nuk është në pritje");
        }
        app.setStatus(ApplicationStatus.REJECTED);
        app.setDecidedAt(LocalDateTime.now());
        app.setDecidedById(userId);
        notificationService.notifyEmail(app.getApplicant(), "Aplikimi u refuzua", "Thirrja \"" + request.getTitle() + "\" u refuzua.");
        return toResponse(applicationRepo.save(app));
    }

    public org.springframework.data.domain.Page<ApplicationResponse> getByRequestId(Long requestId, org.springframework.data.domain.Pageable pageable) {
        if (!helpRequestRepo.existsById(requestId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Thirrja nuk u gjend");
        }
        return applicationRepo.findByHelpRequestId(requestId, pageable).map(this::toResponse);
    }

    public org.springframework.data.domain.Page<ApplicationResponse> getByApplicantId(Long userId, org.springframework.data.domain.Pageable pageable) {
        return applicationRepo.findByApplicantId(userId, pageable).map(this::toResponse);
    }

    @org.springframework.transaction.annotation.Transactional
    public ApplicationResponse withdraw(Long userId, Long applicationId) {
        Application app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aplikimi nuk u gjet"));
        if (app.getApplicant() == null || !app.getApplicant().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nuk je aplikuesi i këtij aplikimi");
        }
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vetëm aplikimet në pritje mund të tërhiqen");
        }
        app.setStatus(ApplicationStatus.WITHDRAWN);
        app.setDecidedAt(LocalDateTime.now());
        app.setDecidedById(userId);
        Application saved = applicationRepo.save(app);
        HelpRequest req = saved.getHelpRequest();
        if (req != null) {
            notificationService.notifyEmail(req.getOwner(), "Aplikimi u tërhoq", "Përdoruesi e tërhoqi aplikimin për \"" + req.getTitle() + "\"");
            notificationService.notifySms(req.getOwner(), "Aplikimi u tërhoq për \"" + req.getTitle() + "\"");
        }
        return toResponse(saved);
    }

    private ApplicationResponse toResponse(Application app) {
        return ApplicationResponse.builder()
                .applicationId(app.getId())
                .helpRequestId(app.getHelpRequest() != null ? app.getHelpRequest().getId() : null)
                .applicantId(app.getApplicant() != null ? app.getApplicant().getId() : null)
                .applicantName(app.getApplicant() != null ? (app.getApplicant().getFirstName() + " " + app.getApplicant().getLastName()) : null)
                .applicantPhone(app.getApplicant() != null ? app.getApplicant().getPhone() : null)
                .status(app.getStatus() != null ? app.getStatus().name() : null)
                .message(app.getMessage())
                .build();
    }
}
