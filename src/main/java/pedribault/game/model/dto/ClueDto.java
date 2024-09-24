package pedribault.game.model.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.MissionOptionSummary;
import pedribault.game.model.dto.summary.MissionSummary;

@Getter
@Setter
public class ClueDto {
    private Integer id;
    private String content;
    private Integer order;
    private Integer subOrder;
    private MissionSummary mission;
    private MissionOptionSummary missionOption;
}
