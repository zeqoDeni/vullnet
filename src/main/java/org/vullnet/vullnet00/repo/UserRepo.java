package org.vullnet.vullnet00.repo;

import org.vullnet.vullnet00.model.User;
import org.vullnet.vullnet00.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {
     Optional<User> findByEmail(String email);
     boolean existsByEmail(String email);
     boolean existsByRole(org.vullnet.vullnet00.model.Role role);
     List<User> findByRole(Role role);
     org.springframework.data.domain.Page<User> findAllByOrderByRewardPointsDesc(org.springframework.data.domain.Pageable pageable);
     long countByRoleAndActiveTrue(org.vullnet.vullnet00.model.Role role);


}
