package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MissionSummaryPlayer {
    private StandardMissionSummary standardMissionSummary;
    private List<ClueSummary> clues;
}
