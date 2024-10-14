package pedribault.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedribault.game.model.EscapePlayerMapping;

import java.util.List;
import java.util.Optional;

public interface EscapePlayerMappingRepository extends JpaRepository<EscapePlayerMapping, Integer> {

    List<EscapePlayerMapping> findByPlayerId(Integer playerId);
    List<EscapePlayerMapping> findByEscapeId(Integer escapeId);

    Optional<EscapePlayerMapping> findByPlayerIdAndEscapeId(Integer playerId, Integer escapeId);
}
