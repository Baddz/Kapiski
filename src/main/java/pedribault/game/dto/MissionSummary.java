package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionTypeEnum;

@Getter
@Setter
public class MissionSummary {
    private Integer id;
    private MissionTypeEnum type;
    private String title;
    private String description;
    private Integer order;
    private Boolean isVisible;
    private Boolean isOptional;
}
