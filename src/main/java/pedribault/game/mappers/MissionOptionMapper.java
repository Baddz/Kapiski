package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pedribault.game.enums.MissionConditionEnum;
import pedribault.game.model.MissionOption;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateMissionOption;
import pedribault.game.model.dto.CreateOrUpdate.UpdateMissionOption;
import pedribault.game.model.dto.MissionOptionDto;

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
        // Transform into mutable object
        final List<String> conditions = new ArrayList<>();
        if (missionOption.getConditions() != null) {
            for (MissionConditionEnum missionConditionEnum : missionOption.getConditions()) {
                conditions.add(missionConditionEnum.name());
            }
        }
        missionOptionDto.setConditions(conditions);
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

    public CreateOrUpdateMissionOption updateMissionOptionToCreateOrUpdateMissionOption(UpdateMissionOption updateMissionOption) {
        final CreateOrUpdateMissionOption createOrUpdateMissionOption = new CreateOrUpdateMissionOption();
        createOrUpdateMissionOption.setClues(updateMissionOption.getClues());
        createOrUpdateMissionOption.setConditions(updateMissionOption.getConditions());
        createOrUpdateMissionOption.setDescription(updateMissionOption.getDescription());
        return createOrUpdateMissionOption;
    }
}
