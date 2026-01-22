package service;

import dto.UserCreateRequest;
import dto.UserResponse;
import lombok.RequiredArgsConstructor;
import model.Role;
import model.User;
import org.hibernate.tool.schema.spi.ExceptionHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import repo.Repo;

@Service
@RequiredArgsConstructor
public class UserService {
    final private Repo repo;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public UserCreateRequest create(UserCreateRequest req) {
        if (repo.existsByEmail(req.getEmail())) {
             throw new RuntimeException("email egziston");
        } User.builder().firstName(req.getFirstName()).lastName(req.getLastName()).email(req.getEmail()).passwordHash(passwordEncoder.encode(req.getPassword())).role(Role.USER).build();
    }

}
