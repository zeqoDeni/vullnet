package org.vullnet.vullnet00.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vullnet.vullnet00.model.*;
import org.vullnet.vullnet00.repo.ApplicationRepo;
import org.vullnet.vullnet00.repo.BlogPostRepo;
import org.vullnet.vullnet00.repo.HelpRequestRepo;
import org.vullnet.vullnet00.repo.UserRepo;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final HelpRequestRepo helpRequestRepo;
    private final ApplicationRepo applicationRepo;
    private final BlogPostRepo blogPostRepo;

    public DevDataSeeder(UserRepo userRepo, HelpRequestRepo helpRequestRepo, ApplicationRepo applicationRepo, BlogPostRepo blogPostRepo) {
        this.userRepo = userRepo;
        this.helpRequestRepo = helpRequestRepo;
        this.applicationRepo = applicationRepo;
        this.blogPostRepo = blogPostRepo;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() == 0) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User owner = userRepo.save(User.builder()
                .firstName("Arber")
                .lastName("Hoxha")
                .email("owner@example.com")
                .passwordHash(encoder.encode("password123"))
                .phone("+35561000001")
                .role(Role.USER)
                .build());

        User volunteer = userRepo.save(User.builder()
                .firstName("Elira")
                .lastName("Krasniqi")
                .email("volunteer@example.com")
                .passwordHash(encoder.encode("password123"))
                .phone("+35561000002")
                .role(Role.USER)
                .build());

            HelpRequest request1 = helpRequestRepo.save(HelpRequest.builder()
                    .title("Ndihmë për pazaret")
                    .description("Dikush të më ndihmojë të marr ushqimet e porositura.")
                    .location("Prishtinë")
                    .status(RequestStatus.OPEN)
                    .statusUpdatedAt(java.time.LocalDateTime.now())
                    .owner(owner)
                    .createdBy(owner)
                    .build());

            helpRequestRepo.save(HelpRequest.builder()
                    .title("Transport drejt ambulancës")
                    .description("Kam nevojë për transport nesër në mëngjes.")
                    .location("Prizren")
                    .status(RequestStatus.OPEN)
                    .statusUpdatedAt(java.time.LocalDateTime.now())
                    .owner(owner)
                    .createdBy(owner)
                    .build());

            applicationRepo.save(Application.builder()
                    .helpRequest(request1)
                    .applicant(volunteer)
                    .message("Mund të vij pasdite.")
                    .status(ApplicationStatus.PENDING)
                    .build());
        }

        if (blogPostRepo.count() == 0) {
            blogPostRepo.save(org.vullnet.vullnet00.model.BlogPost.builder()
                    .title("Si të menaxhosh vullnetarët")
                    .slug("si-te-menaxhosh-vullnetaret")
                    .summary("Këshilla praktike për thirrjet dhe komunikimin.")
                    .content("Përcakto qartë nevojat, mbaj komunikim të hapur dhe falëndero vullnetarët për kontributin e tyre.")
                    .coverImageUrl("https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=1200&q=80")
                    .authorName("Stafi Vullnet")
                    .published(true)
                    .build());
            blogPostRepo.save(org.vullnet.vullnet00.model.BlogPost.builder()
                    .title("Siguria në terren")
                    .slug("siguria-ne-terren")
                    .summary("Si të mbrohesh gjatë aktiviteteve vullnetare.")
                    .content("Mbaj kontakte emergjence, informo të tjerët për itinerarin dhe përdor pajisje sigurie kur është e nevojshme.")
                    .coverImageUrl("https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1200&q=80")
                    .authorName("Stafi Vullnet")
                    .published(true)
                    .build());
        }
    }
}
