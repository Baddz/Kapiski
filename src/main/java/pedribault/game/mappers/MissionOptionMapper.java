package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pedribault.game.model.MissionOption;
import pedribault.game.model.dto.MissionOptionDto;
import pedribault.game.model.dto.summary.MissionOptionSummary;

import java.util.ArrayList;
import java.util.List;

@Component
public class MissionOptionMapper {

    @Autowired
    private ToSummaryMapper toSummaryMapper;

    public MissionOptionDto missionOptionToMissionOptionDto(MissionOption missionOption) {
        final MissionOptionDto missionOptionDto = new MissionOptionDto();
        missionOptionDto.setMission(toSummaryMapper.missionToMissionSummary(missionOption.getMission()));
        missionOptionDto.setId(missionOption.getId());
        missionOptionDto.setClues(toSummaryMapper.cluesToClueSummaries(missionOption.getClues()));
        missionOptionDto.setDescription(missionOption.getDescription());
        return missionOptionDto;
    }

    public List<MissionOptionDto> missionOptionsToMissionOptionDtos(List<MissionOption> missionOptions) {
        final List<MissionOptionDto> missionOptionDtos = new ArrayList<>();
        if (missionOptions != null) {
            for (MissionOption missionOption : missionOptions) {
                missionOptionDtos.add(missionOptionToMissionOptionDto(missionOption));
            }
        }
        return missionOptionDtos;
    }
}
