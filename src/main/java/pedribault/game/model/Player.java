package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionConditionEnum;
import pedribault.game.mappers.MissionConditionEnumConverter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Players")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "mail")
    private String mail;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
    @Column(name = "comment")
    private String comment;
    @Column(name = "prefers_email")
    private Boolean prefersEmail;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_context_id")
    private PlayerContext playerContext;

    @ManyToMany(mappedBy = "players")
    private List<Escape> escapes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "Player_J_Sidekick",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "sidekick_id")
    )
    @JsonManagedReference
    private List<Sidekick> sidekicks;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MissionPlayerMapping> missionMappings = new ArrayList<>();

    public void addSidekick(Sidekick sidekick) {
        if (sidekick == null) {
            return;
        }
        if (this.sidekicks == null) {
            this.sidekicks = new ArrayList<>();
        }
        if (this.sidekicks.contains(sidekick)) {
            this.getSidekicks().add(sidekick);
        }

        if (sidekick.getPlayers() == null) {
            sidekick.setPlayers(new ArrayList<>());
        }
        if (!sidekick.getPlayers().contains(this)) {
            sidekick.getPlayers().add(this);
        }
    }

    public void removeSidekick(Sidekick sidekick) {
        if (this.sidekicks.contains(sidekick)) {
            this.sidekicks.remove(sidekick);
        }
        if (sidekick.getPlayers().contains(this)) {
            sidekick.getPlayers().remove(this);
        }
    }

    public boolean isMissionApplicable(Mission mission) {
        return mission.getMissionContext() == null || 
               playerContext.isMissionContextApplicable(mission.getMissionContext());
    }
}
