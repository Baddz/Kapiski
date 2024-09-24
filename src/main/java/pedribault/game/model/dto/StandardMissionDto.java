package pedribault.game.model.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.EscapeSummary;
import pedribault.game.model.dto.summary.MissionOptionSummary;
import pedribault.game.enums.MissionConditionEnum;

import java.util.Map;

@Getter
@Setter
public class StandardMissionDto {
    private Double successRate;
    private EscapeSummary escape;
    private Map<MissionConditionEnum, MissionOptionSummary> options;
}
