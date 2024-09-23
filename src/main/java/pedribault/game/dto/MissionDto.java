package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.dto.summary.ClueSummary;
import pedribault.game.dto.summary.EscapeSummary;

import java.util.List;

@Getter
@Setter
public class MissionDto {
    private Integer id;
    private String title;
    private String description;
    private Integer order;
    private Boolean isVisible;
    private Boolean isOptional;
    private EscapeSummary escape;
    private List<ClueSummary> clues;
    private List<PlayerSummary> players;
}
