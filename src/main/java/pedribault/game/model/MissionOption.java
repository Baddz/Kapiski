package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// This class handles the different options a mission can be resolved, to ease the personalization od a standard mission

@Entity
@Getter
@Setter
@Table(name = "Mission_Options")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MissionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description")
    private String description; // ex : "Receive a package", "Go to a location", "Get info by email"

    @OneToMany(mappedBy = "missionOption", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clue> clues;

    @ManyToOne
    @JoinColumn(name = "standard_mission_id")
    private StandardMission standardMission;

    public void addClue(Clue clue) {
        if (this.getClues() == null) {
            this.setClues(new ArrayList<>());
        }
        this.getClues().add(clue);
    }

    public void addClues(List<Clue> clues) {
        if (this.getClues() == null) {
            this.setClues(new ArrayList<>());
        }
        this.getClues().addAll(clues);
    }

    public void removeClue(Clue clue) {
        if (this.getClues() == null) {
            this.setClues(new ArrayList<>());
        }

        if (clue != null) {
            if (this.getClues().contains(clue)) {
                this.getClues().remove(clue);
            }
        }
    }
}
