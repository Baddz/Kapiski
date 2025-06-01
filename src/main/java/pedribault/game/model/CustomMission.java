package pedribault.game.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@DiscriminatorValue("CUSTOM")
public class CustomMission extends Mission {
    // For missions added between standard missions: order = previous mission's order, subOrder = 1,2,3...
    // For replacement missions: order = replaced mission's order, subOrder = null
    @Column(name = "sub_order")
    private Integer subOrder;

    // Nullable - only set when this custom mission replaces a standard mission
    @ManyToOne
    @JoinColumn(name = "replaced_mission_id", nullable = true)
    private StandardMission replacedMission;

    @ElementCollection
    @CollectionTable(
        name = "custom_mission_contexts",
        joinColumns = @JoinColumn(name = "custom_mission_id")
    )
    @Column(name = "context")
    private Set<String> applicableContexts = new HashSet<>();

    public void addApplicableContext(String context) {
        if (this.applicableContexts == null) {
            this.applicableContexts = new HashSet<>();
        }
        this.applicableContexts.add(context);
    }

    public void removeApplicableContext(String context) {
        if (this.applicableContexts != null) {
            this.applicableContexts.remove(context);
        }
    }
}
