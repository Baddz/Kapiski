package pedribault.game.model.dto.CreateOrUpdate;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrUpdateMissionOption {
    private String description;
    private List<String> conditions;
    private List<CreateOrUpdateClue> clues;
}
