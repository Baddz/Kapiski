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
@Table(name = "Missions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public abstract class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "visible")
    private Boolean isVisible;
    @Column(name = "optional")
    private Boolean isOptional;
    @Column(name = "`order`")
    private Integer order;
    @Column(name = "score")
    private Integer score;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clue> clues;

    @ManyToOne
    @JoinColumn(name = "escape_id")
    private Escape escape;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MissionOption> options;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MissionPlayerMapping> playerMappings = new ArrayList<>();

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private MissionContext missionContext;

    public List<MissionOption> getApplicableOptions(PlayerContext playerContext) {
        final List<MissionOption> missionOptions = new ArrayList<>();
        for (MissionOption missionOption : options) {
            if (missionOption.isApplicable(playerContext)) {
                missionOptions.add(missionOption);
            }
        }
        return missionOptions;
    }

    public void addClue(Clue clue) {
        if (this.getClues() == null) {
            this.setClues(new ArrayList<>());
        }
        if (!this.clues.contains(clue)) {
            this.clues.add(clue);
        }
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

    public void removeOption(MissionOption missionOption) {
        if (this.getOptions() == null) {
            this.setOptions(new ArrayList<>());
        }

        if (missionOption != null) {
            if (this.getOptions().contains(missionOption)) {
                this.getOptions().remove(missionOption);
            }
        }
    }

    public void addPlayerMapping(MissionPlayerMapping mapping) {
        if (this.playerMappings == null) {
            this.playerMappings = new ArrayList<>();
        }
        if (!this.playerMappings.contains(mapping)) {
            this.playerMappings.add(mapping);
        }
    }

    public void removePlayerMapping(MissionPlayerMapping mapping) {
        if (this.playerMappings != null && this.playerMappings.contains(mapping)) {
            this.playerMappings.remove(mapping);
        }
    }

    public void addMissionOption(MissionOption missionOption) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.getOptions().add(missionOption);
    }
}
