package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.dto.ClueDto;
import pedribault.game.dto.ClueSummary;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Clue;
import pedribault.game.model.StandardMission;
import pedribault.game.repository.MissionRepository;

import java.util.List;

@Component
public class ClueMapper {

    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private MissionMapper missionMapper;

    public ClueDto clueToClueDto(Clue clue) {
        final ClueDto clueDTO = new ClueDto();
        clueDTO.setId(clue.getId());
        clueDTO.setOrder(clue.getOrder());
        clueDTO.setContent(clue.getContent());
        if (clue.getStandardMission() != null) {
            clueDTO.setMissionSummary(missionMapper.standardMissionToMissionSummary(clue.getStandardMission()));
        }
        return clueDTO;
    }

    public Clue clueDtoToClue(ClueDto clueDTO) {
        final Clue clue = new Clue();
        if (clueDTO.getId() != null) {
            clue.setId(clueDTO.getId());
        }
        clue.setOrder(clueDTO.getOrder());
        clue.setContent(clueDTO.getContent());
        if (clueDTO.getMissionSummary() != null) {
            StandardMission standardMission = missionRepository.findById(clueDTO.getMissionSummary().getId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Missions table", "The id " + clueDTO.getMissionSummary().getId() + " does not exist."));
            clue.setStandardMission(standardMission);
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
