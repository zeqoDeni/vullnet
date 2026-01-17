package repo;

import model.HelpRequest;
import model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HelpRequestRepo extends JpaRepository<HelpRequest, Long> {
    boolean existsByApplicantId(Long applicantId);
    Optional <HelpRequest> findByApplicantId(Long applicantId);
    Optional <HelpRequest> findByOwner(User user);

}
