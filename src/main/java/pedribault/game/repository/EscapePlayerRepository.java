package pedribault.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EscapePlayerRepository extends JpaRepository<EscapePlayer, Integer> {

    // Méthode pour trouver un EscapePlayer par id_escape et id_player
    Optional<EscapePlayer> findByEscapeIdAndPlayerId(Integer escapeId, Integer playerId);
}