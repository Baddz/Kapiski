package pedribault.game.model;

import jakarta.persistence.*;
import pedribault.game.enums.MissionStatusEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Player_J_Mission")
@IdClass(PlayerMissionId.class)
public class PlayerMission {

    @Id
    @Column(name = "id_player", insertable = false, updatable = false)
    private int playerId;
    @Id
    @Column(name = "id_mission", insertable = false, updatable = false)
    private int missionId;

    @ManyToOne
    @MapsId("idMission")
    @JoinColumn(name = "id_mission")
    private Mission mission;

    @ManyToOne
    @MapsId("idPlayer")
    @JoinColumn(name = "id_player")
    private Player player;

    @Enumerated(EnumType.STRING)
    private MissionStatusEnum status;

    public PlayerMission() {}

    public PlayerMission(Integer playerId, Integer missionId, MissionStatusEnum status) {
        this.playerId = playerId;
        this.missionId = missionId;
        this.status = status;
        this.missionId = mission.getId();
        this.playerId = player.getId();
    }

    @Transient
    public int getMissionId() {
        return mission != null ? mission.getId() : null;
    }

    @Transient
    public int getPlayerId() {
        return player != null ? player.getId() : null;
    }
}
