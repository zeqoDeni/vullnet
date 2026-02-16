package org.vullnet.vullnet00.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.vullnet.vullnet00.model.RequestMessage;

public interface RequestMessageRepo extends JpaRepository<RequestMessage, Long> {
    Page<RequestMessage> findByHelpRequestIdOrderByCreatedAtAsc(Long helpRequestId, Pageable pageable);
    void deleteByHelpRequestId(Long helpRequestId);
}
