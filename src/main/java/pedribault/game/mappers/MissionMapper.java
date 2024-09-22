package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pedribault.game.dto.MissionSummary;
import pedribault.game.dto.MissionSummaryEscape;
import pedribault.game.model.StandardMission;

import java.util.List;

@Component
public class MissionMapper {

    @Autowired
    private ClueMapper clueMapper;

    public MissionSummary standardMissionToMissionSummary(StandardMission standardMission) {
        final MissionSummary missionSummary = new MissionSummary();
        missionSummary.setId(standardMission.getId());
        missionSummary.setMissionOrder(standardMission.getMissionOrder());
        missionSummary.setOptional(standardMission.getOptional());
        missionSummary.setVisible(standardMission.getVisible());
        missionSummary.setTitle(standardMission.getTitle());
        missionSummary.setSuccessRate(standardMission.getSuccessRate());
        return missionSummary;
    }

    public List<MissionSummary> standardMissionsToMissionSummaries(List<StandardMission> standardMissions) {
        return standardMissions.stream().map(m -> standardMissionToMissionSummary(m)).toList();
    }

    public MissionSummaryEscape standardMissionToMissionSummaryEscape(StandardMission standardMission) {
        final MissionSummaryEscape missionSummaryEscape = new MissionSummaryEscape();
        final MissionSummary missionSummary = new MissionSummary();
        missionSummary.setId(standardMission.getId());
        missionSummary.setMissionOrder(standardMission.getMissionOrder());
        missionSummary.setOptional(standardMission.getOptional());
        missionSummary.setVisible(standardMission.getVisible());
        missionSummary.setTitle(standardMission.getTitle());
        missionSummary.setSuccessRate(standardMission.getSuccessRate());
        missionSummaryEscape.setMissionSummary(missionSummary);
        missionSummaryEscape.setClues(clueMapper.cluesToClueSummaries(standardMission.getClues()));
        return missionSummaryEscape;
    }

    public List<MissionSummaryEscape> standardMissionsToMissionSummaryEscapes(List<StandardMission> standardMissions) {
        return standardMissions.stream().map(m -> standardMissionToMissionSummaryEscape(m)).toList();
    }

}
