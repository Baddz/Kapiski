package pedribault.game.model.dto.CreateOrUpdate;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionTypeEnum;
import pedribault.game.model.dto.summary.MissionOptionSummary;

import java.util.List;

@Getter
@Setter
public class CreateOrUpdateMission {
    private String missionType;
    private String title;
    private String description;
    private Boolean isVisible;
    private Boolean isOptional;
    private Integer order;
    private List<Integer> clueIds;
    private List<MissionOptionSummary> missionOptions;
    // if type == "CUSTOM"
    private Integer subOrder;
    private List<Integer> escapeIds;
    private List<Integer> playerIds;
    // if type == "STANDARD"
    private Double successRate;
    private Integer escapeId;
}
