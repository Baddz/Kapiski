package pedribault.game.repository;

import pedribault.game.model.Clue;
import pedribault.game.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClueRepository extends JpaRepository<Clue, Integer> {
    List<Clue> findByMissionOrderByOrderAscSubOrderAsc(Mission mission);
    List<Clue> findByMissionAndOrderOrderBySubOrderAsc(Mission mission, Integer order);
    void deleteByMission(Mission mission);
}
