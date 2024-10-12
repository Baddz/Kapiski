package pedribault.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedribault.game.model.PlayerPreference;

public interface PlayerPreferenceRepository extends JpaRepository<PlayerPreference, Integer> {
}
