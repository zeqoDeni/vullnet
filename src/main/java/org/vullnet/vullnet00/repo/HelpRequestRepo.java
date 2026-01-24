package org.vullnet.vullnet00.repo;

import org.vullnet.vullnet00.model.HelpRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpRequestRepo extends JpaRepository<HelpRequest, Long> {
    Page<HelpRequest> findByOwnerId(Long ownerId, Pageable pageable);
    Page<HelpRequest> findByStatus(org.vullnet.vullnet00.model.RequestStatus status, Pageable pageable);
    Page<HelpRequest> findByOwnerIdAndStatus(Long ownerId, org.vullnet.vullnet00.model.RequestStatus status, Pageable pageable);
    long countByOwnerIdAndStatus(Long ownerId, org.vullnet.vullnet00.model.RequestStatus status);
    long countByStatus(org.vullnet.vullnet00.model.RequestStatus status);
}
