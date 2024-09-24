package pedribault.game.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.CustomMissionSummary;
import pedribault.game.model.dto.summary.EscapeSummary;
import pedribault.game.model.dto.summary.SidekickSummary;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Data
public class PlayerDto {
    private Integer id;
    private String name;
    private String firstName;
    private String mail;
    private String address;
    private String phone;
    private String comment;
    private List<String> preferences;
    private List<SidekickSummary> sidekicks;
    private List<CustomMissionSummary> customMissions;
    private List<EscapeSummary> escapes;
    private Map<EscapeSummary, List<CustomMissionSummary>> escapeCustomMissionsMap;
}
