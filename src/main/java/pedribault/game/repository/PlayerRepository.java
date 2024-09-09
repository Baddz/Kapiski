package pedribault.game.repository;

import pedribault.game.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    Optional<Player> findByNameIgnoreCaseAndFirstNameIgnoreCase(String name, String firstName);
}
