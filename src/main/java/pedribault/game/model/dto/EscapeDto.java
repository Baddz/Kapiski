package pedribault.game.model.dto;

import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.PlayerSummary;
import pedribault.game.model.dto.summary.StandardMissionSummary;
import pedribault.game.model.dto.summary.UniverseSummary;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EscapeDto {
    private Integer id;
    private String title;
    private Double successRate;
    private Integer difficulty;
    private UniverseSummary universe;
    private List<StandardMissionSummary> missions;
    private List<PlayerSummary> players;

    public void addPlayerDto(PlayerSummary playerSummary) {
        if (this.getPlayers() == null) {
            this.setPlayers(new ArrayList<>());
        }
        this.getPlayers().add(playerSummary);
    }

    public void addMission(StandardMissionSummary missionSummary) {
        if (this.getMissions() == null) {
            this.setMissions(new ArrayList<>());
        }
        this.getMissions().add(missionSummary);
    }
}
