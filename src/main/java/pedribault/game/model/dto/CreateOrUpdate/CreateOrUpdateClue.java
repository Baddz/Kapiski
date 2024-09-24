package pedribault.game.model.dto.CreateOrUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrUpdateClue {
    private String content;
    private Integer order;
    private Integer subOrder;
    private Integer missionId;
    private Integer missionOptionId;
}
