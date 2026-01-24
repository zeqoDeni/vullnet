package org.vullnet.vullnet00.service;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.vullnet.vullnet00.dto.ApplicationCreateRequest;
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


    public Application apply(Long userId, Long requestId, ApplicationCreateRequest req) {
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
        return applicationRepo.save(app);
    }
}
