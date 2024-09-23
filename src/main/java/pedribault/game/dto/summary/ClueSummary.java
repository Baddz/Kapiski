package pedribault.game.dto.summary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClueSummary {
    private Integer id;
    private String content;
    private Integer order;
    private Integer subOrder;
}
