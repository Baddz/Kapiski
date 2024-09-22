package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionTypeEnum;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@DiscriminatorValue("CUSTOM")
public class CustomMission extends Mission {

    // customMission either replace a standardMission, or are added between two
    // replacement --> custom order = standard order, subOrder = null
    // added after standardmission with standard order --> custom order = standard order, subOrder = 0, 1, 2 ...
    // we can add several mission after mission standard order (subOrder increases with each)
    @Column(name = "sub_order")
    private Integer subOrder;

    @ManyToOne
    @JoinColumn(name = "escape_id")
    private Escape escape;
}
