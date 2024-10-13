package pedribault.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedribault.game.model.EscapePlayerMapping;

public interface EscapePlayerMappingRepository extends JpaRepository<EscapePlayerMapping, Integer> {
}
