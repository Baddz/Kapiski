package pedribault.game.model.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.EscapeSummary;
import pedribault.game.model.dto.summary.PlayerSummary;

import java.time.LocalDateTime;

@Getter
@Setter
public class EscapePlayerMappingDto {
    private Integer id;
    private PlayerSummary player;
    private EscapeSummary escape;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}
