package pedribault.game.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class PlayerMissionId implements Serializable {

    private Integer idPlayer;
    private Integer idMission;

    public PlayerMissionId() {}

    public PlayerMissionId(Integer idPlayer, Integer idMission) {
        this.idPlayer = idPlayer;
        this.idMission = idMission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerMissionId that = (PlayerMissionId) o;
        return Objects.equals(idPlayer, that.idPlayer) &&
                Objects.equals(idMission, that.idMission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPlayer, idMission);
    }
}
