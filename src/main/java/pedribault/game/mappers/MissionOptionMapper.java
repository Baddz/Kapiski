package pedribault.game.mappers;

import org.springframework.stereotype.Component;
import pedribault.game.model.MissionOption;
import pedribault.game.model.dto.summary.MissionOptionSummary;

@Component
public class MissionOptionMapper {

    private MissionMapper missionMapper;

    public MissionOptionSummary missionOptionToMissionOptionSummary(MissionOption missionOption) {
        final MissionOptionSummary missionOptionSummary = new MissionOptionSummary();
        missionOptionSummary.setId(missionOption.getId());
        missionOptionSummary.setMission(missionMapper.missionToMissionSummary(missionOption.getMission()));
        missionOptionSummary.setDescription(missionOption.getDescription());
        return missionOptionSummary;
    }
}
