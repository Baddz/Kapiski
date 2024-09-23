package pedribault.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pedribault.game.model.MissionOption;

@Repository
public interface MissionOptionRepository extends JpaRepository<MissionOption, Integer> {
}
