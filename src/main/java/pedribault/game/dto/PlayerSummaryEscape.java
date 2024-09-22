package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.EscapeStatusEnum;

@Getter
@Setter
public class PlayerSummaryEscape {
    private PlayerSummary playerSummary;
    private EscapeStatusEnum status;
}
