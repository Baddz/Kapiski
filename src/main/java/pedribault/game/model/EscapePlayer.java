package pedribault.game.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.EscapeStatusEnum;

@Entity
@Getter
@Setter
@Table(name = "Escape_J_Player")
public class EscapePlayer {

    @EmbeddedId
    private EscapePlayerId id = new EscapePlayerId();

    @ManyToOne
    @MapsId("escapeId")
    @JoinColumn(name = "id_escape")
    private Escape escape;

    @ManyToOne
    @MapsId("playerId")
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
        this.id = new EscapePlayerId(escape.getId(), player.getId());
    }

    @Transient
    public Integer getIdEscape() {
        return escape != null ? escape.getId() : null;
    }

    @Transient
    public Integer getIdPlayer() {
        return player != null ? player.getId() : null;
    }
}
