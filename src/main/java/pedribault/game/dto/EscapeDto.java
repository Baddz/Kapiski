package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;

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
    private List<MissionSummary> missions;
    private List<PlayerStatus> players;

    public void addEscapePlayerDto(PlayerStatus playerStatus) {
        if (this.getPlayers() == null) {
            this.setPlayers(new ArrayList<>());
        }
        this.getPlayers().add(playerStatus);
    }
}
