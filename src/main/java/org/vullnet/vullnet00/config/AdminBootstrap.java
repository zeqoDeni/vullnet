package org.vullnet.vullnet00.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vullnet.vullnet00.model.Role;
import org.vullnet.vullnet00.model.User;
import org.vullnet.vullnet00.repo.UserRepo;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.core.env.Environment environment;

    @Override
    public void run(String... args) {
        String email = environment.getProperty("app.admin.email");
        String password = environment.getProperty("app.admin.password");
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return;
        }
        if (userRepo.existsByRole(Role.ADMIN)) {
            return;
        }
        if (userRepo.existsByEmail(email)) {
            return;
        }
        String firstName = environment.getProperty("app.admin.first-name", "Admin");
        String lastName = environment.getProperty("app.admin.last-name", "User");
        User admin = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .build();
        userRepo.save(admin);
    }
}
