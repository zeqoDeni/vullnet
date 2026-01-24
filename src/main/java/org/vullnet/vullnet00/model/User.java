package org.vullnet.vullnet00.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash; // store hashed passwords only

    @Column(length = 500)
    private String bio;

    @Column
    private String avatarUrl;

    @Column
    private String location;

    @Column(length = 40)
    private String phone;

    @Column(length = 500)
    private String skills;

    @Column
    @Builder.Default
    private Boolean availability = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean profilePublic = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "createdBy")
    private List<HelpRequest> helpRequests;



    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }


}
