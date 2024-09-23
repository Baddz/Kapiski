package pedribault.game.dto.Clue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClueCreate {
    private String content;
    private Integer order;
    private Integer subOrder;
    private Integer missionId;
    private Integer missionOptionId;
}
