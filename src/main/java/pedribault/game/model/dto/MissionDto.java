package pedribault.game.model.dto;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionConditionEnum;
import pedribault.game.model.dto.summary.ClueSummary;
import pedribault.game.model.dto.summary.EscapeSummary;
import pedribault.game.model.dto.summary.MissionOptionSummary;
import pedribault.game.model.dto.summary.PlayerSummary;

import java.util.List;
import java.util.Map;

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
    private List<ClueSummary> clues;
    private List<MissionOptionSummary> options;
}
