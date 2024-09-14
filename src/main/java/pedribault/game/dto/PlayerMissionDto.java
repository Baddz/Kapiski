package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionStatusEnum;

@Getter
@Setter
public class PlayerMissionDto {
    private Integer playerId;
    private Integer missionId;
    private MissionStatusEnum status;
}
