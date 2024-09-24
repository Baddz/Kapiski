package pedribault.game.mappers;

import org.springframework.stereotype.Component;
import pedribault.game.enums.MissionTypeEnum;
import pedribault.game.model.CustomMission;
import pedribault.game.model.Mission;
import pedribault.game.model.StandardMission;
import pedribault.game.model.dto.summary.CustomMissionSummary;
import pedribault.game.model.dto.summary.MissionSummary;
import pedribault.game.model.dto.summary.StandardMissionSummary;

import java.util.List;

@Component
public class MissionMapper {

    public MissionSummary missionToMissionSummary(Mission mission) {
        MissionSummary missionSummary = null;
        if (mission instanceof final StandardMission standardMission) {
            StandardMissionSummary standardMissionSummary = new StandardMissionSummary();
            standardMissionSummary.setSuccessRate(standardMission.getSuccessRate());
            missionSummary = standardMissionSummary;
            missionSummary.setType(MissionTypeEnum.STANDARD.name());
        } else if (mission instanceof final CustomMission customMission) {
            CustomMissionSummary customMissionSummary = new CustomMissionSummary();
            customMissionSummary.setSubOrder(customMission.getSubOrder());
            missionSummary = customMissionSummary;
            missionSummary.setType(MissionTypeEnum.CUSTOM.name());
        }
        missionSummary.setId(mission.getId());
        missionSummary.setOrder(mission.getOrder());
        missionSummary.setIsOptional(mission.getIsOptional());
        missionSummary.setIsVisible(mission.getIsVisible());
        missionSummary.setTitle(mission.getTitle());
        missionSummary.setDescription(mission.getDescription());
        return missionSummary;
    }

    public List<MissionSummary> missionsToMissionSummaries(List<StandardMission> standardMissions) {
        return standardMissions.stream().map(this::missionToMissionSummary).toList();
    }

}
