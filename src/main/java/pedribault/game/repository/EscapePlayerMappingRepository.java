package pedribault.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedribault.game.model.EscapePlayerMapping;

import java.util.List;

public interface EscapePlayerMappingRepository extends JpaRepository<EscapePlayerMapping, Integer> {

    List<EscapePlayerMapping> findByPlayerId(Integer playerId);
    List<EscapePlayerMapping> findByEscapeId(Integer escapeId);

    EscapePlayerMapping findByPlayerIdAndEscapeId(Integer playerId, Integer escapeId);
}
