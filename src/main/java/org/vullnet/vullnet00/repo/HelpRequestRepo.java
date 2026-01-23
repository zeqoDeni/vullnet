package org.vullnet.vullnet00.repo;

import org.vullnet.vullnet00.model.HelpRequest;
import org.vullnet.vullnet00.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HelpRequestRepo extends JpaRepository<HelpRequest, Long> {

}
