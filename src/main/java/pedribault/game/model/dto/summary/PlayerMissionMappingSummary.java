package pedribault.game.model.dto.summary;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PlayerMissionMappingSummary {
    private Integer id;
    private Integer missionId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}
