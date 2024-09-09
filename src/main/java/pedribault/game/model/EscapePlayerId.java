package pedribault.game.model;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class EscapePlayerId implements Serializable {

    private int idEscape;
    private int idPlayer;

    public EscapePlayerId() {
    }

    public EscapePlayerId(int idEscape, int idPlayer) {
        this.idEscape = idEscape;
        this.idPlayer = idPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EscapePlayerId that = (EscapePlayerId) o;
        return idEscape == that.idEscape &&
                idPlayer == that.idPlayer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEscape, idPlayer);
    }
}
