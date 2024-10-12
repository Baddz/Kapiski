package pedribault.game.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.*;

import java.util.List;

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
    private PlayerContextSummary playerContext;
    private List<String> preferences;
    private List<SidekickSummary> sidekicks;
    private List<CustomMissionSummary> customMissions;
    private List<EscapeSummary> escapes;
    private List<PlayerMissionMappingSummary> missionMappings;
}
