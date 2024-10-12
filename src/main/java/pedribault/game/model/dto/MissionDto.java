package pedribault.game.model.dto;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.*;

import java.util.List;

@Getter
@Setter
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public class MissionDto {
    private Integer id;
    private String title;
    private String description;
    private Integer order;
    private Boolean isVisible;
    private Boolean isOptional;
    private EscapeSummary escape;
    private List<ClueSummary> clues;
    private List<MissionOptionSummary> options;
    private List<MissionPlayerMappingSummary> playerMappings;
}
