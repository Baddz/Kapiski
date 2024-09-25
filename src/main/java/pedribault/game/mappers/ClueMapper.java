package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pedribault.game.model.dto.ClueDto;
import pedribault.game.model.Clue;
import pedribault.game.model.Mission;
import pedribault.game.model.MissionOption;
import pedribault.game.model.dto.summary.ClueSummary;
import pedribault.game.repository.MissionRepository;

import java.util.List;

@Component
public class ClueMapper {

    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private MissionOptionMapper missionOptionMapper;
    @Autowired
    private MissionMapper missionMapper;

    public ClueDto clueToClueDto(Clue clue) {
        final ClueDto clueDTO = new ClueDto();
        clueDTO.setId(clue.getId());
        clueDTO.setOrder(clue.getOrder());
        clueDTO.setSubOrder(clue.getSubOrder());
        clueDTO.setContent(clue.getContent());
        if (clue.getMission() != null) {
            clueDTO.setMission(missionMapper.missionToMissionSummary(clue.getMission()));
        }
        if (clue.getMissionOption() != null) {
            clueDTO.setMissionOption(missionOptionMapper.missionOptionToMissionOptionSummary(clue.getMissionOption()));
        }
        return clueDTO;
    }

    public Clue clueDtoToClue(ClueDto clueDTO, Mission mission, MissionOption missionOption) {
        final Clue clue = new Clue();
        if (clueDTO.getId() != null) {
            clue.setId(clueDTO.getId());
        }
        clue.setOrder(clueDTO.getOrder());
        clue.setContent(clueDTO.getContent());
        if (mission != null) {
            clue.setMission(mission);
        }
        return clue;
    }

    public ClueSummary clueToClueSummary(Clue clue) {
        final ClueSummary clueSummary = new ClueSummary();
        clueSummary.setId(clue.getId());
        clueSummary.setContent(clue.getContent());
        clueSummary.setOrder(clue.getOrder());
        return clueSummary;
    }

    public List<ClueSummary> cluesToClueSummaries(List<Clue> clues) {
        return clues.stream().map(c -> clueToClueSummary(c)).toList();
    }

}
