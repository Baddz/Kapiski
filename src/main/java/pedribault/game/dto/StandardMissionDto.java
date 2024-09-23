package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.dto.summary.EscapeSummary;
import pedribault.game.enums.MissionConditionEnum;

import java.util.Map;

@Getter
@Setter
public class StandardMissionDto {
    private Double successRate;
    private EscapeSummary escape;
    private Map<MissionConditionEnum, MissionOptionSummary> options;
}
