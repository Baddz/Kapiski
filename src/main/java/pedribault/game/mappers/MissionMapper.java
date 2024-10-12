package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pedribault.game.model.CustomMission;
import pedribault.game.model.Mission;
import pedribault.game.model.MissionOption;
import pedribault.game.model.StandardMission;
import pedribault.game.model.dto.CustomMissionDto;
import pedribault.game.model.dto.MissionDto;
import pedribault.game.model.dto.StandardMissionDto;
import pedribault.game.model.dto.summary.MissionOptionSummary;

import java.util.ArrayList;
import java.util.List;

@Component
public class MissionMapper {

    @Autowired
    private ToSummaryMapper toSummaryMapper;

    public MissionDto missionToMissionDto(Mission mission) {
        MissionDto missionDto = null;
        if (mission instanceof final StandardMission standardMission) {
            StandardMissionDto standardMissionDto = new StandardMissionDto();
            standardMissionDto.setSuccessRate(standardMission.getSuccessRate());
            missionDto = standardMissionDto;
        } else if (mission instanceof final CustomMission customMission) {
            CustomMissionDto customMissionDto = new CustomMissionDto();
            customMissionDto.setSubOrder(customMission.getSubOrder());
            missionDto = customMissionDto;
        }
        missionDto.setId(mission.getId());
        missionDto.setTitle(mission.getTitle());
        missionDto.setDescription(mission.getDescription());
        missionDto.setOrder(mission.getOrder());
        missionDto.setIsVisible(mission.getIsVisible());
        missionDto.setIsOptional(mission.getIsOptional());
        missionDto.setEscape(toSummaryMapper.escapeToEscapeSummary(mission.getEscape()));
        missionDto.setClues(toSummaryMapper.cluesToClueSummaries(mission.getClues()));
        missionDto.setPlayerMappings(toSummaryMapper.missionPlayerMappingsToMissionPlayerMappingSummaries(mission.getPlayerMappings()));
        if (mission.getOptions() != null) {
            final List<MissionOptionSummary> missionOptionSummaries = new ArrayList<>();
            for (MissionOption missionOption: mission.getOptions()) {
                missionOptionSummaries.add(toSummaryMapper.missionOptionToMissionOptionSummary(missionOption));
            }
            missionDto.setOptions(missionOptionSummaries);
        }

        return missionDto;
    }

}
