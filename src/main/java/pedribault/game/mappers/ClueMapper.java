package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.dto.ClueDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Clue;
import pedribault.game.model.StandardMission;
import pedribault.game.repository.MissionRepository;

@Component
public class ClueMapper {

    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private ToSummaryMapper toSummaryMapper;

    public ClueDto clueToClueDto(Clue clue) {
        final ClueDto clueDTO = new ClueDto();
        clueDTO.setId(clue.getId());
        clueDTO.setOrder(clue.getOrder());
        clueDTO.setContent(clue.getContent());
        if (clue.getMission() != null) {
            clueDTO.setMission(toSummaryMapper.missionToMissionSummaryClue(clue.getMission()));
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
        if (clueDTO.getMission() != null) {
            StandardMission standardMission = missionRepository.findById(clueDTO.getMission().getId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Missions table", "The id " + clueDTO.getMission().getId() + " does not exist."));
            clue.setMission(standardMission);
        }
        return clue;
    }


}
