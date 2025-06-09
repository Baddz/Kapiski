package pedribault.game.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pedribault.game.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Integer> {
    
    @Query("SELECT m.id FROM StandardMission m " +
           "WHERE m.escape.id = :escapeId " +
           "AND m.order = :order " +
           "AND m.id != :missionId")
    List<Integer> findStandardMissionIdsWithSameOrderInEscape(
        @Param("escapeId") Integer escapeId,
        @Param("order") Integer order,
        @Param("missionId") Integer missionId
    );

    @Query("SELECT m.id FROM CustomMission m " +
           "WHERE m.escape.id = :escapeId " +
           "AND m.order = :order " +
           "AND m.subOrder = :subOrder " +
           "AND m.id != :missionId")
    List<Integer> findCustomMissionIdsWithSameOrderInEscape(
        @Param("escapeId") Integer escapeId,
        @Param("order") Integer order,
        @Param("subOrder") Integer subOrder,
        @Param("missionId") Integer missionId
    );

    @Query("SELECT COALESCE(MAX(m.subOrder), -1) + 1 FROM CustomMission m " +
           "WHERE m.order = :order")
    Optional<Integer> findNextAvailableSubOrderForOrder(@Param("order") Integer order);

    @Query("SELECT COALESCE(MAX(m.order), -1) + 1 FROM Mission m " +
           "WHERE m.escape.id = :escapeId")
    Optional<Integer> findNextAvailableOrderForEscape(@Param("escapeId") Integer escapeId);
}
