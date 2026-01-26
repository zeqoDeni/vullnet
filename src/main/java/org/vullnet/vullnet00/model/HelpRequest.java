package org.vullnet.vullnet00.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "help_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HelpRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column
    private java.time.LocalDateTime statusUpdatedAt;

    @Column
    private String imageUrl;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="created_by_id")
    private User createdBy;


//Me mappedBy e lidhim aplikimin me request-in. Me cascade=ALL nese ruaj/fshij request-in, veprohet edhe mbi aplikimet. Me orphanRemoval=true nese nje aplikim hiqet nga lista (nuk i perket me request-it), ai fshihet nga databaza.”

    @JsonIgnore
    @OneToMany(mappedBy = "helpRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Application> applications = new java.util.ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_volunteer_id")
    @JsonIgnore
    private User acceptedVolunteer;

    @Column
    private Long acceptedApplicationId;

    @Column
    private java.time.LocalDateTime completedAt;



}
