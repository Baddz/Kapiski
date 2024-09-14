package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.EscapeStatusEnum;

@Getter
@Setter
public class EscapePlayerDto {
    private int escapeId;
    private int playerId;
    private EscapeStatusEnum status;
}
