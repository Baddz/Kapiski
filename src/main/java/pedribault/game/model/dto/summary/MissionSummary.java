package pedribault.game.model.dto.summary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissionSummary {
    private Integer id;
    private String type;
    private String title;
    private String description;
    private Integer order;
    private Boolean isVisible;
    private Boolean isOptional;
}
