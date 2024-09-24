package pedribault.game.model.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.ClueSummary;
import pedribault.game.model.dto.summary.StandardMissionSummary;

import java.util.List;

@Getter
@Setter
public class MissionOptionDto {
    private Integer id;
    private String description;
    private List<ClueSummary> clues;
    private StandardMissionSummary standardMission;
}
