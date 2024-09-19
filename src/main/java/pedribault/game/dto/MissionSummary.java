package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MissionSummary {
    private Integer id;
    private String title;
    private Boolean visible;
    private Integer missionOrder;
    private Double successRate;
    private Boolean optional;
}
