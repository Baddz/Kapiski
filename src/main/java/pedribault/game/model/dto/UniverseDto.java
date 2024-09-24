package pedribault.game.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.model.dto.summary.EscapeSummary;

import java.util.List;

@Getter
@Setter
@Data
public class UniverseDto {
    private Integer id;
    private String title;
    private List<EscapeSummary> escapes;
}
