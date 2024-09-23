package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
@Table(name = "Sidekicks")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Sidekick {

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
    @Column(name = "comment")
    private String comment;
    @Column(name = "phone")
    private String phone;

    @ManyToMany(mappedBy = "sidekicks")
    @JsonBackReference
    private List<Player> players = new ArrayList<>();

    public void addPlayer(Player player) {
        if (player == null) {
            return;
        }
        if (this.players == null) {
            this.players = new ArrayList<>();
        }
        if (!this.players.contains(player)) {
            this.players.add(player);
        }

        if (player.getSidekicks() == null) {
            player.setSidekicks(new ArrayList<>());
        }
        if (!player.getSidekicks().contains(this)) {
            player.getSidekicks().add(this);
        }
    }

    public void removePlayer(Player player) {
        if (this.players.contains(player)) {
            this.players.remove(player);
        }
        if (player.getSidekicks().contains(this)) {
            player.getSidekicks().remove(this);
        }
    }
}
