package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.EscapeStatusEnum;

@Getter
@Setter
public class PlayerStatus {
    private Integer playerId;
    private EscapeStatusEnum status;
}
