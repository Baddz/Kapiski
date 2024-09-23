package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.dto.Clue.ClueCreate;
import pedribault.game.dto.ClueDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Clue;
import pedribault.game.model.Mission;
import pedribault.game.model.MissionOption;
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

}
