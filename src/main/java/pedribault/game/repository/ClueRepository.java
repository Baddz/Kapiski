package pedribault.game.repository;

import pedribault.game.model.Clue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClueRepository extends JpaRepository<Clue, Integer> {
}
