package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pedribault.game.enums.MissionConditionEnum;
import pedribault.game.model.CustomMission;
import pedribault.game.model.Mission;
import pedribault.game.model.MissionOption;
import pedribault.game.model.StandardMission;
import pedribault.game.model.dto.CustomMissionDto;
import pedribault.game.model.dto.MissionDto;
import pedribault.game.model.dto.StandardMissionDto;
import pedribault.game.model.dto.summary.MissionOptionSummary;

import java.util.HashMap;
import java.util.Map;

@Component
public class MissionMapper {

    @Autowired
    private ToSummaryMapper toSummaryMapper;

    public MissionDto missionToMissionDto(Mission mission) {
        MissionDto missionDto = null;
        if (mission instanceof final StandardMission standardMission) {
            StandardMissionDto standardMissionDto = new StandardMissionDto();
            standardMissionDto.setSuccessRate(standardMission.getSuccessRate());
            standardMissionDto.setEscape(toSummaryMapper.escapeToEscapeSummary(standardMission.getEscape()));
            missionDto = standardMissionDto;
        } else if (mission instanceof final CustomMission customMission) {
            CustomMissionDto customMissionDto = new CustomMissionDto();
            customMissionDto.setSubOrder(customMission.getSubOrder());
            customMissionDto.setEscapes(toSummaryMapper.escapesToEscapeSummaries(customMission.getEscapes()));
            customMissionDto.setPlayers(toSummaryMapper.playersToPlayerSummaries(customMission.getPlayers()));
            missionDto = customMissionDto;
        }
        missionDto.setId(mission.getId());
        missionDto.setTitle(mission.getTitle());
        missionDto.setDescription(mission.getDescription());
        missionDto.setOrder(mission.getOrder());
        missionDto.setIsVisible(mission.getIsVisible());
        missionDto.setIsOptional(mission.getIsOptional());
        missionDto.setClues(toSummaryMapper.cluesToClueSummaries(mission.getClues()));
        if (mission.getOptions() != null) {
            final Map<MissionConditionEnum, MissionOptionSummary> mapSummary = new HashMap<>();
            for (MissionConditionEnum missionConditionEnum : mission.getOptions().keySet()) {
                mapSummary.put(missionConditionEnum,
                        toSummaryMapper.missionOptionToMissionOptionSummary(mission.getOptions().get(missionConditionEnum)));
            }
            missionDto.setOptions(mapSummary);
        }

        return missionDto;
    }

}
