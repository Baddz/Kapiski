package pedribault.game.model.dto.CreateOrUpdate;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrUpdateEscape {
    private String title;
    private Double successRate;
    private Integer difficulty;
    private Integer universeId;
    private List<Integer> missionIds;
    private List<Integer> playerIds;
}
