package pedribault.game.model.dto.CreateOrUpdate;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateOrUpdateEscapePlayerMapping {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}
