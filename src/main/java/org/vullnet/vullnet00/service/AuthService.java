package org.vullnet.vullnet00.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.vullnet.vullnet00.dto.AuthLoginRequest;
import org.vullnet.vullnet00.dto.AuthRegisterRequest;
import org.vullnet.vullnet00.dto.AuthResponse;
import org.vullnet.vullnet00.dto.UserResponse;
import org.vullnet.vullnet00.model.Role;
import org.vullnet.vullnet00.model.User;
import org.vullnet.vullnet00.repo.UserRepo;
import org.vullnet.vullnet00.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(AuthRegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email-i është në përdorim");
        }
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .build();
        User saved = userRepo.save(user);
        String token = jwtService.generateToken(saved);
        return AuthResponse.builder()
                .token(token)
                .user(toResponse(saved))
                .build();
    }

    public AuthResponse login(AuthLoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kredencialet janë të pasakta"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kredencialet janë të pasakta");
        }
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(toResponse(user))
                .build();
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .role(u.getRole() != null ? u.getRole().name() : null)
                .active(u.isActive())
                .rewardPoints(u.getRewardPoints())
                .completedRequests(u.getCompletedRequests())
                .averageRating(u.getAverageRating())
                .reviewCount(u.getReviewCount())
                .phone(u.getPhone())
                .badges(computeBadges(u))
                .build();
    }

    private java.util.List<String> computeBadges(User u) {
        java.util.List<String> list = new java.util.ArrayList<>();
        int pts = u.getRewardPoints() != null ? u.getRewardPoints() : 0;
        int completed = u.getCompletedRequests() != null ? u.getCompletedRequests() : 0;
        double rating = u.getAverageRating() != null ? u.getAverageRating() : 0.0;
        if (pts >= 200) list.add("Kampion");
        else if (pts >= 100) list.add("Aktiv");
        if (completed >= 5) list.add("I besueshëm");
        if (rating >= 4.5 && u.getReviewCount() != null && u.getReviewCount() >= 5) list.add("Vlerësim i lartë");
        return list;
    }
}
