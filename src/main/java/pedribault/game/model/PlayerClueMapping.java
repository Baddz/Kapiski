package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "Player_J_Clue")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class PlayerClueMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "clue_id")
    private Clue clue;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @ManyToOne
    @JoinColumn(name = "mission_player_mapping_id")
    private MissionPlayerMapping missionPlayerMapping;  // To track which mission this clue was given for
} 