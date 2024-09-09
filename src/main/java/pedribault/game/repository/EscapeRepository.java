package pedribault.game.repository;

import pedribault.game.model.Escape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EscapeRepository extends JpaRepository<Escape, Integer> {
}
