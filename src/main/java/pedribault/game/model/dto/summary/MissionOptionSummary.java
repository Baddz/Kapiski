package pedribault.game.model.dto.summary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissionOptionSummary {
    private Integer id;
    private String description;
    private MissionSummary mission;
}
