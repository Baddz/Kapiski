package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionStatusEnum;

@Getter
@Setter
public class PlayerSummaryMission {
    private PlayerSummary playerSummary;
    private MissionStatusEnum status;
}
