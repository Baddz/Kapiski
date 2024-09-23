package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.dto.summary.ClueSummary;
import pedribault.game.model.Clue;

import java.util.List;

@Getter
@Setter
public class MissionOptionSummary {
    private Integer id;
    private String description;
    private StandardMissionSummary mission;
}
