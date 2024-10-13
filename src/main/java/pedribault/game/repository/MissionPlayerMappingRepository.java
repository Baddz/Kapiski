package pedribault.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedribault.game.model.MissionPlayerMapping;

public interface MissionPlayerMappingRepository extends JpaRepository<MissionPlayerMapping, Integer> {
}
