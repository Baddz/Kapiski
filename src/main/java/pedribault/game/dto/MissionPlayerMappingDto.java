package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.dto.summary.CustomMissionSummary;

@Getter
@Setter
public class MissionPlayerMappingDto {
    private Integer id;
    private PlayerSummary player;
    private StandardMissionSummary standardMission;
    private CustomMissionSummary customMission;
    private String status;
}
