package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.dto.summary.CustomMissionSummary;

import java.util.List;

@Getter
@Setter
public class StandardMissionCustomizationRuleDto {
    private Integer id;
    private String description;
    private StandardMissionSummary standardMission;
    private CustomMissionSummary customMission;
}
