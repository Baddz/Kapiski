package pedribault.game.model.dto.summary;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MissionOptionSummary {
    private Integer id;
    private String description;
    private List<String> conditions;
    private MissionSummary mission;
}
