package pedribault.game.model.dto.summary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerContextSummary {
    private Integer id;
    private Integer sidekicks;
    private Boolean prefersEmail;
}
