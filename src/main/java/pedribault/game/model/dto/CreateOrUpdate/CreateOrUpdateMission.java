package pedribault.game.model.dto.CreateOrUpdate;

import lombok.Getter;
import lombok.Setter;

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
    private Integer escapeId;
    private List<CreateOrUpdateClueWithId> clues;
    private List<CreateOrUpdateMissionOption> missionOptions;
    // if type == "CUSTOM"
    private Integer subOrder;
    // if type == "STANDARD"
    private Double successRate;
}
