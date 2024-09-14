package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.dto.ClueDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Clue;
import pedribault.game.model.Mission;
import pedribault.game.repository.MissionRepository;

@Component
public class ClueMapper {

    @Autowired
    private MissionRepository missionRepository;

    public ClueDto clueToClueDto(Clue clue) {
        final ClueDto clueDTO = new ClueDto();
        clueDTO.setId(clue.getId());
        clueDTO.setOrder(clue.getOrder());
        clueDTO.setContent(clue.getContent());
        if (clue.getMission() != null) {
            clueDTO.setMissionId(clue.getMission().getId());
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
        if (clueDTO.getMissionId() != null) {
            Mission mission = missionRepository.findById(clueDTO.getMissionId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Missions table", "The id " + clueDTO.getMissionId() + " does not exist."));
            clue.setMission(mission);
        }
        return clue;
    }
}
