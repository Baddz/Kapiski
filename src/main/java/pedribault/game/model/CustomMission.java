package pedribault.game.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

}
