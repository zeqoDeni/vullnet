package org.vullnet.vullnet00.repo;

import org.vullnet.vullnet00.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepo  extends JpaRepository<Application, Long> {
    Optional<Application> findByHelpRequestIdAndApplicantId(Long helpRequestId, Long applicantId);
    boolean existsByHelpRequestIdAndApplicantId(Long helpRequestId, Long applicantId);
}
