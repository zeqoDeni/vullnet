package org.vullnet.vullnet00.service;

import org.vullnet.vullnet00.dto.UserCreateRequest;
import org.vullnet.vullnet00.dto.UserResponse;
import org.vullnet.vullnet00.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.vullnet.vullnet00.model.Role;
import org.vullnet.vullnet00.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.vullnet.vullnet00.repo.Repo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    final private Repo repo;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public UserResponse create(UserCreateRequest req) {
        if (repo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("email already in use");

        } User user = User.builder().firstName(req.getFirstName()).lastName(req.getLastName()).email(req.getEmail()).passwordHash(passwordEncoder.encode(req.getPassword())).role(Role.USER).build();
    return toResponse(repo.save(user));
    }

    public List <UserResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse getById(Long id) {
        User u = repo.findById(id).orElseThrow(()-> new RuntimeException("ky user nuk egziston sipas ksaj id"));
        return toResponse(u);
    }

    public UserResponse update(Long id, UserUpdateRequest req) {
        User u = repo.findById(id).orElseThrow(() -> new RuntimeException("ky user nuk egziston sipas ksaj id"));
        if (req.getEmail() != null && !req.getEmail().equals(u.getEmail())){
            if (repo.existsByEmail(req.getEmail())) {
                throw new RuntimeException("emaili egziston");
            }
            u.setEmail(req.getEmail());
            }
        if (req.getFirstName() != null) { u.setFirstName(req.getFirstName());}

        if (req.getLastName() !=null) {u.setLastName(req.getFirstName());}

        if (req.getPassword()!= null && !req.getPassword().isBlank()) {
            passwordEncoder.encode(req.getPassword());
        }

        return toResponse(repo.save(u));
        }
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        repo.deleteById(id);
    }




    private UserResponse toResponse(User u) {
        return UserResponse.builder().id(u.getId()).firstName(u.getFirstName()).lastName(u.getLastName()).email(u.getEmail()).role(u.getRole() !=null ? u.getRole().name() : null).build();
    }

}
