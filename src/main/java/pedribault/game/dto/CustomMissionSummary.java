package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomMissionSummary {
    private Integer id;
    private String title;
    private String description;
    private Boolean isVisible;
    private Integer order;
    private Integer suborder;
    private Integer subOrder;
    private Double successRate;
    private Boolean isOptional;

    private EscapeSummaryMission escape;
}