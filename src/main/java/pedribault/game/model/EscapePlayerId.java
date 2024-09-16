package pedribault.game.model;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class EscapePlayerId implements Serializable {

    private Integer escapeId;
    private Integer playerId;

    public EscapePlayerId() {
    }

    public EscapePlayerId(int escapeId, int playerId) {
        this.escapeId = escapeId;
        this.playerId = playerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EscapePlayerId that = (EscapePlayerId) o;
        return escapeId == that.escapeId &&
                playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(escapeId, playerId);
    }
}
