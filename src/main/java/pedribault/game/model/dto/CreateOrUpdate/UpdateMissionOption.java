package pedribault.game.model.dto.CreateOrUpdate;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateMissionOption {
    private Integer id;
    private String description;
    private List<String> conditions;
    private List<UpdateClue> clues;
}
