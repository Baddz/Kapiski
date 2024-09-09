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
    private int idPlayer;
    @Id
    @Column(name = "id_mission", insertable = false, updatable = false)
    private int idMission;

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

    public PlayerMission(Integer idPlayer, Integer idMission, MissionStatusEnum status) {
        this.idPlayer = idPlayer;
        this.idMission = idMission;
        this.status = status;
        this.idMission = mission.getId();
        this.idPlayer = player.getId();
    }

    @Transient
    public int getIdMission() {
        return mission != null ? mission.getId() : null;
    }

    @Transient
    public int getIdPlayer() {
        return player != null ? player.getId() : null;
    }
}
