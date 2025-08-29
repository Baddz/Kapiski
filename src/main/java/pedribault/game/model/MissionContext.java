package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "Mission_Context")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MissionContext {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "required_sidekicks")
    private Integer requiredSidekicks;
    @Column(name = "needs_interaction_strangers")
    private Boolean needsInteractionStrangers;
    @Column(name = "needs_interaction_friends")
    private Boolean needsInteractionFriends;
    @Column(name = "needs_interaction_family")
    private Boolean needsInteractionFamily;
    @Column(name = "needs_interaction_close")
    private Boolean needsInteractionClose;
    @Column(name = "needs_address")
    private Boolean needsValidAddress;
    @Column(name = "needs_email")
    private Boolean needsEmail;
    @Column(name = "needs_app")
    private Boolean needsApp;

    @OneToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;
}
