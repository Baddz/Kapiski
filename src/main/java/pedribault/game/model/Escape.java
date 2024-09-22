package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Escapes")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Escape {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "success_rate")
    private Double successRate;
    @Column(name = "difficulty")
    private Integer difficulty;

    @ManyToOne
    @JoinColumn(name = "id_universe")
    private Universe universe;

    @OneToMany(mappedBy = "escape", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StandardMission> standardMissions = new ArrayList<>();

    @OneToMany(mappedBy = "escape", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EscapePlayer> escapePlayers = new ArrayList<>();

    public Escape() {
        this.standardMissions = new ArrayList<>();
        this.escapePlayers = new ArrayList<>();
    }

    public void addEscapePlayer(EscapePlayer escapePlayer) {
        if (this.getEscapePlayers() == null) {
            this.setEscapePlayers(new ArrayList<>());
        }
        this.getEscapePlayers().add(escapePlayer);
    }

    public void addMission(StandardMission standardMission) {
        if (this.getStandardMissions() == null) {
            this.setStandardMissions(new ArrayList<>());
        }
        this.getStandardMissions().add(standardMission);
    }

    public void removeMission(StandardMission standardMission) {
        if (this.getStandardMissions().contains(standardMission)) {
            this.getStandardMissions().remove(standardMission);
        }
    }
}
