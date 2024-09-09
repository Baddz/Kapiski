package pedribault.game.repository;

import pedribault.game.model.PlayerMissionId;
import pedribault.game.model.PlayerMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerMissionRepository extends JpaRepository<PlayerMission, PlayerMissionId> {

    Optional<PlayerMission> findByPlayerIdAndMissionId(Integer playerId, Integer missionId);

}
