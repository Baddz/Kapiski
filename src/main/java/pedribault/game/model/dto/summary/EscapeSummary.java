package pedribault.game.model.dto.summary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EscapeSummary {
    private Integer id;
    private String title;
    private Double successRate;
    private Integer difficulty;
}
