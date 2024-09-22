package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Custom_Missions")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CustomMission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title")
    private String customTitle;
    @Column(name = "desription")
    private String customDescription;

    // customMission either replace a standardMission, or are added between two
    // replacement --> custom order = standard order, subOrder = null
    // added after standardmission with standard order --> custom order = standard order, subOrder = 0, 1, 2 ...
    // we can add several mission after mission standard order (subOrder increases with each)
    @Column(name = "order")
    private Integer order;
    // ordre
    @Column(name = "subOrder")
    private Integer subOrder;

    @ManyToOne
    @JoinColumn(name = "standard_mission_id", nullable = true)
    private StandardMission standardMission;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "escape_id")
    private Escape escape;

}
