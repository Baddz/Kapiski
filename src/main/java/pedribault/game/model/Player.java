package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Players", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "first_name"})})
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

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerMission> playerMissions;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EscapePlayer> escapePlayers;

    @ManyToMany
    @JoinTable(
            name = "Player_J_Sidekick",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "sidekick_id")
    )
    @JsonManagedReference
    private List<Sidekick> sidekicks;

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
}
