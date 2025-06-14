package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionConditionEnum;

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

    @Column(name = "priority")
    private Integer priority;

    @OneToMany(mappedBy = "missionOption", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clue> clues = new ArrayList<>();

    @ElementCollection(targetClass = MissionConditionEnum.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "Mission_Conditions",
            joinColumns = @JoinColumn(name = "mission_option_id")
    )
    @Column(name = "`condition`")
    private List<MissionConditionEnum> conditions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    public boolean isApplicable(PlayerContext playerContext) {
        for (MissionConditionEnum condition : conditions) {
            switch (condition) {
                case HAS_A_FRIEND_INVOLVED:
                    if (playerContext.getSidekicks() < 0) {
                        return false;
                    }
                    break;
                case HAS_TWO_FRIENDS_INVOLVED:
                    if (playerContext.getSidekicks() < 1) {
                        return false;
                    }
                    break;
                case HAS_MORE_FRIENDS_INVOLVED:
                    if (playerContext.getSidekicks() < 2) {
                        return false;
                    }
                    break;
                case PREFERS_EMAIL:
                    if (!playerContext.getPrefersEmail()) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    public void addClue(Clue clue) {
        if (this.getClues() == null) {
            this.setClues(new ArrayList<>());
        }
        clue.setMission(null);
        this.getClues().add(clue);
        clue.setMissionOption(this);
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
                clue.setMissionOption(null);
            }
        }
    }

    public void addCondition(MissionConditionEnum conditionEnum) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<>();
        }

        this.getConditions().add(conditionEnum);
    }

    public void removeCondition(MissionConditionEnum conditionEnum) {
        if (conditionEnum != null) {
            if (this.getConditions().contains(conditionEnum)) {
                this.getConditions().remove(conditionEnum);
            }
        }
    }
}
