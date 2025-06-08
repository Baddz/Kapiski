package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Player_Context")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class PlayerContext {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sidekicks")
    private Integer sidekicks;
    @Column(name = "prefers_email")
    private Boolean prefersEmail;
    @Column(name = "interaction_strangers")
    private Boolean interactionStrangers;
    @Column(name = "interaction_friends")
    private Boolean interactionFriends;
    @Column(name = "valid_address")
    private Boolean validAddress;
    @Column(name = "has_email")
    private Boolean hasEmail;
    @Column(name = "has_app")
    private Boolean hasApp;

    @OneToOne(mappedBy = "playerContext")
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    public boolean isMissionContextApplicable(MissionContext missionContext) {
        return (missionContext.getRequiredSidekicks() == null || sidekicks >= missionContext.getRequiredSidekicks())
                && (!missionContext.getNeedsInteractionStrangers() || interactionStrangers)
                && (!missionContext.getNeedsInteractionFriends() || interactionFriends)
                && (!missionContext.getNeedsAddress() || validAddress)
                && (!missionContext.getNeedsEmail() || hasEmail)
                && (!missionContext.getNeedsApp() || hasApp);
    }
}
