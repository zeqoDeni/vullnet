package model;

import jakarta.persistence.*;
import lombok.*;
import org.apache.logging.log4j.util.Lazy;

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


}
