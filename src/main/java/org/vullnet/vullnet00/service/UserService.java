package org.vullnet.vullnet00.service;

import org.vullnet.vullnet00.dto.UserCreateRequest;
import org.vullnet.vullnet00.dto.UserProfileResponse;
import org.vullnet.vullnet00.dto.UserProfileUpdateRequest;
import org.vullnet.vullnet00.dto.UserResponse;
import org.vullnet.vullnet00.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.vullnet.vullnet00.model.Role;
import org.vullnet.vullnet00.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vullnet.vullnet00.repo.UserRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    final private UserRepo repo;
    private final PasswordEncoder passwordEncoder;
    public UserResponse create(UserCreateRequest req) {
        if (repo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("email already in use");

        } User user = User.builder().firstName(req.getFirstName()).lastName(req.getLastName()).email(req.getEmail()).passwordHash(passwordEncoder.encode(req.getPassword())).role(Role.USER).build();
    return toResponse(repo.save(user));
    }

    public UserResponse updateRole(Long userId, Role role) {
        User user = repo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        return toResponse(repo.save(user));
    }

    public UserProfileResponse getProfile(Long userId) {
        User user = repo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return toProfileResponse(user);
    }

    public UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest req) {
        User user = repo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (req.getBio() != null) {
            user.setBio(req.getBio());
        }
        if (req.getAvatarUrl() != null) {
            user.setAvatarUrl(req.getAvatarUrl());
        }
        if (req.getLocation() != null) {
            user.setLocation(req.getLocation());
        }
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone());
        }
        if (req.getSkills() != null) {
            user.setSkills(req.getSkills());
        }
        if (req.getAvailability() != null) {
            user.setAvailability(req.getAvailability());
        }
        if (req.getProfilePublic() != null) {
            user.setProfilePublic(req.getProfilePublic());
        }
        return toProfileResponse(repo.save(user));
    }

    public UserResponse updateStatus(Long userId, boolean active) {
        User user = repo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(active);
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

        if (req.getLastName() !=null) {u.setLastName(req.getLastName());}

        if (req.getPassword()!= null && !req.getPassword().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
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
        return UserResponse.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .role(u.getRole() != null ? u.getRole().name() : null)
                .active(u.isActive())
                .build();
    }

    private UserProfileResponse toProfileResponse(User u) {
        return UserProfileResponse.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .bio(u.getBio())
                .avatarUrl(u.getAvatarUrl())
                .location(u.getLocation())
                .phone(u.getPhone())
                .skills(u.getSkills())
                .availability(Boolean.TRUE.equals(u.getAvailability()))
                .profilePublic(u.isProfilePublic())
                .build();
    }


}
