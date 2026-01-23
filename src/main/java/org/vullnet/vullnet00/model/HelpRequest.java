package org.vullnet.vullnet00.model;

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
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name="created_by_id")
    private User createdBy;


//Me mappedBy e lidhim aplikimin me request-in. Me cascade=ALL nese ruaj/fshij request-in, veprohet edhe mbi aplikimet. Me orphanRemoval=true nese nje aplikim hiqet nga lista (nuk i perket me request-it), ai fshihet nga databaza.”

    @OneToMany(mappedBy = "helpRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Application> applications = new java.util.ArrayList<>();




}
