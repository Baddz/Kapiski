package pedribault.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedribault.game.model.PlayerContext;

public interface PlayerContextRepository extends JpaRepository<PlayerContext, Integer> {
}
