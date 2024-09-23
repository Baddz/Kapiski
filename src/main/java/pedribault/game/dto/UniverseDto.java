package pedribault.game.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.dto.summary.EscapeSummary;

import java.util.List;

@Getter
@Setter
@Data
public class UniverseDto {
    private Integer id;
    private String title;
    private List<EscapeSummary> escapes;
}
