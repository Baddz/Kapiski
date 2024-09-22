package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MissionSummaryEscape {
    private MissionSummary missionSummary;
    private List<ClueSummary> clues;
    private List<PlayerSummaryMission> players;
}
