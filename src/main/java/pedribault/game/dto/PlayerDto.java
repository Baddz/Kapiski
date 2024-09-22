package pedribault.game.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class PlayerDto {
    private Integer id;
    private String mail;
    private String address;
    private String name;
    private String firstName;
    private String phone;
    private String comment;
    private List<SidekickSummary> sidekicks;
    private List<StandardMissionSummary> missions;
    private List<Integer> escapeIds;
}
