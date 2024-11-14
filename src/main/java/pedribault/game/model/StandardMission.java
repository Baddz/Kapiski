package pedribault.game.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Getter
@Setter
@DiscriminatorValue("STANDARD")
public class StandardMission extends Mission {

    @Column(name = "success_rate")
    private Double successRate;
}
