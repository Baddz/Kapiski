package pedribault.game.mappers;

import org.springframework.stereotype.Component;
import pedribault.game.dto.MissionSummary;
import pedribault.game.model.Mission;

import java.util.List;

@Component
public class MissionMapper {

    public MissionSummary missionToMissionSummary(Mission mission) {
        final MissionSummary missionSummary = new MissionSummary();
        missionSummary.setId(mission.getId());
        missionSummary.setMissionOrder(mission.getMissionOrder());
        missionSummary.setOptional(mission.getOptional());
        missionSummary.setVisible(mission.getVisible());
        missionSummary.setTitle(mission.getTitle());
        missionSummary.setSuccessRate(mission.getSuccessRate());
        return missionSummary;
    }

    public List<MissionSummary> missionsToMissionSummaries(List<Mission> missions) {
        return missions.stream().map(m -> missionToMissionSummary(m)).toList();
    }
}
