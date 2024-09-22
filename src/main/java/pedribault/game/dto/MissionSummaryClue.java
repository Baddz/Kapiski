package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionTypeEnum;

@Getter
@Setter
public class MissionSummaryClue {
    private Integer id;
    private MissionTypeEnum missionType;
    private String title;
    private String description;
    private Boolean isVisible;
    private Boolean isOptional;
    private Integer order;
    private Integer subOrder;
    private EscapeSummaryMission escape;
    private Double successRate;
}
