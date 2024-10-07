package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionConditionEnum;
import pedribault.game.enums.MissionTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clue> clues;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
    private List<MissionOption> options;

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
