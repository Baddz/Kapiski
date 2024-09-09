package pedribault.game.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.EscapeStatusEnum;

@Entity
@Getter
@Setter
@Table(name = "Escape_J_Player")
@IdClass(EscapePlayerId.class)
public class EscapePlayer {

    @Id
    @Column(name = "id_escape")
    private int idEscape;

    @Id
    @Column(name = "id_player")
    private int idPlayer;

    @ManyToOne
    @MapsId("idEscape")
    @JoinColumn(name = "id_escape")
    private Escape escape;

    @ManyToOne
    @MapsId("idPlayer")
    @JoinColumn(name = "id_player")
    private Player player;

    @Enumerated(EnumType.STRING)
    private EscapeStatusEnum status;

    public EscapePlayer() {
    }

    public EscapePlayer(Player player, Escape escape, EscapeStatusEnum status) {
        this.player = player;
        this.escape = escape;
        this.status = status;
    }

    @Transient
    public int getIdEscape() {
        return escape != null ? escape.getId() : null;
    }

    @Transient
    public int getIdPlayer() {
        return player != null ? player.getId() : null;
    }
}
