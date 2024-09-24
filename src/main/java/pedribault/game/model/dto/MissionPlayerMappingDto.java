package pedribault.game.model.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.CustomMissionSummary;
import pedribault.game.model.dto.summary.MissionSummary;
import pedribault.game.model.dto.summary.PlayerSummary;
import pedribault.game.model.dto.summary.StandardMissionSummary;

import java.time.LocalDateTime;

@Getter
@Setter
public class MissionPlayerMappingDto {
    private Integer id;
    private PlayerSummary player;
    private MissionSummary mission;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}
