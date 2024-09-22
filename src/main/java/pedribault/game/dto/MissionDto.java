package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MissionDto {
    private Integer id;
    private String title;
    private Boolean visible;
    private Integer missionOrder;
    private Double successRate;
    private Boolean optional;
    private Integer escapeId;
    private List<ClueSummary> clues;
    private List<PlayerSummaryMission> players;
}
