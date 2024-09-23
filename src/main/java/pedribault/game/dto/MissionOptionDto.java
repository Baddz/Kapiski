package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.dto.summary.ClueSummary;

import java.util.List;

@Getter
@Setter
public class MissionOptionDto {
    private Integer id;
    private String description;
    private List<ClueSummary> clues;
    private StandardMissionSummary standardMission;
}
