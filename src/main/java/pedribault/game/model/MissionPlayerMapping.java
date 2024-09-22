package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionStatusEnum;

@Entity
@Getter
@Setter
@Table(name = "Player_J_Mission")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MissionPlayerMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "standard_mission_id", nullable = true)
    private StandardMission standardMission;

    @ManyToOne
    @JoinColumn(name = "custom_mission_id", nullable = true)
    private CustomMission customMission;

    @Enumerated(EnumType.STRING)
    private MissionStatusEnum status;
}
