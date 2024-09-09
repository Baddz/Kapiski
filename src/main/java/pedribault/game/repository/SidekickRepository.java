package pedribault.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pedribault.game.model.Sidekick;

import java.util.Optional;

@Repository
public interface SidekickRepository extends JpaRepository<Sidekick, Integer> {
    Optional<Sidekick> findByNameIgnoreCaseAndFirstNameIgnoreCase(String name, String firstName);

}
