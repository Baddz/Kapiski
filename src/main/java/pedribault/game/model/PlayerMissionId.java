package pedribault.game.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class PlayerMissionId implements Serializable {

    private Integer playerId;
    private Integer missionId;

    public PlayerMissionId() {}

    public PlayerMissionId(Integer playerId, Integer missionId) {
        this.playerId = playerId;
        this.missionId = missionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerMissionId that = (PlayerMissionId) o;
        return Objects.equals(playerId, that.playerId) &&
                Objects.equals(missionId, that.missionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, missionId);
    }
}
