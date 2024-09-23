package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Standard_Mission_Customization_Rule")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class StandardMissionCustomizationRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "standard_mission_id", nullable = true)
    private StandardMission standardMission;

    @ManyToOne
    @JoinColumn(name = "custom_mission_id", nullable = true)
    private CustomMission customMission;
}
