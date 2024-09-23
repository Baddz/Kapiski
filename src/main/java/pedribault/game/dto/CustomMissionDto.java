package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.dto.summary.EscapeSummary;

import java.util.List;

@Getter
@Setter
public class CustomMissionDto extends MissionDto {
    private Integer subOrder;
    private List<EscapeSummary> escapes;
    private List<PlayerSummary> players;
}
