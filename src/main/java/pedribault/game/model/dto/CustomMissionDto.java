package pedribault.game.model.dto;

import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DiscriminatorValue("CUSTOM")
public class CustomMissionDto extends MissionDto {
    private Integer subOrder;
}
