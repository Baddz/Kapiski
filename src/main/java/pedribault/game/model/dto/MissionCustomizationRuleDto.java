package pedribault.game.model.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.MissionSummary;

@Getter
@Setter
public class MissionCustomizationRuleDto {
    private Integer id;
    private String description;
    private MissionSummary missionSummary;
}
