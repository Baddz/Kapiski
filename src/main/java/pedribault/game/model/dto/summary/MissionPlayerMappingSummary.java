package pedribault.game.model.dto.summary;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MissionPlayerMappingSummary {
    private Integer id;
    private Integer playerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}
