package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// This class handles the different options a mission can be resolved, to ease the personalization od a standard mission

@Entity
@Getter
@Setter
@Table(name = "Mission_Option")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MissionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description")
    private String description; // ex : "Receive a package", "Go to a location", "Get info by email"

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private StandardMission mission;
}
