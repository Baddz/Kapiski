package pedribault.game.model;



import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionConditionEnum;
import pedribault.game.enums.MissionTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Getter
@Setter
@DiscriminatorValue("STANDARD")
public class StandardMission extends Mission {

    @Column(name = "success_rate")
    private Double successRate;



}
