package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClueDto {
    private Integer id;
    private String content;
    private Integer order;
    private MissionSummaryClue mission;
}
