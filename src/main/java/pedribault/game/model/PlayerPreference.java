package pedribault.game.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionConditionEnum;

@Entity
@Table(name = "Player_Preference")
@Getter
@Setter
public class PlayerPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(name = "preference", nullable = false)
    private MissionConditionEnum preference;
}
