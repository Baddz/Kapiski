package pedribault.game.model.dto;

import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.EscapeSummary;
import pedribault.game.model.dto.summary.PlayerSummary;

import java.util.List;

@Getter
@Setter
@DiscriminatorValue("CUSTOM")
public class CustomMissionDto extends MissionDto {
    private Integer subOrder;
    private EscapeSummary escapes;
    private List<PlayerSummary> players;
}
