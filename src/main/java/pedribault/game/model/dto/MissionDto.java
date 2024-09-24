package pedribault.game.model.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.ClueSummary;
import pedribault.game.model.dto.summary.EscapeSummary;
import pedribault.game.model.dto.summary.PlayerSummary;

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
