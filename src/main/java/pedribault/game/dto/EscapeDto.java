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
    private Integer universeId;
    private List<Integer> missionIds;
    private List<EscapePlayerDto> escapePlayerDtos;

    public void addEscapePlayerDto(EscapePlayerDto escapePlayerDto) {
        if (this.getEscapePlayerDtos() == null) {
            this.setEscapePlayerDtos(new ArrayList<>());
        }
        this.getEscapePlayerDtos().add(escapePlayerDto);
    }
}
