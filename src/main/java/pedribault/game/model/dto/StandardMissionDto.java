package pedribault.game.model.dto;

import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.EscapeSummary;
import pedribault.game.model.dto.summary.MissionOptionSummary;
import pedribault.game.enums.MissionConditionEnum;

import java.util.Map;

@Getter
@Setter
@DiscriminatorValue("STANDARD")
public class StandardMissionDto extends MissionDto {
    private Double successRate;
}
