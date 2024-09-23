package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerMissionDto {
    private Integer playerId;
    private Integer missionId;
    private String status;
}
