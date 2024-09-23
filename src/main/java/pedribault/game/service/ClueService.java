package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pedribault.game.dto.Clue.ClueCreate;
import pedribault.game.dto.ClueDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.ClueMapper;
import pedribault.game.model.Clue;
import pedribault.game.model.Mission;
import pedribault.game.model.MissionOption;
import pedribault.game.model.StandardMission;
import pedribault.game.repository.ClueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pedribault.game.repository.MissionOptionRepository;
import pedribault.game.repository.MissionRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ClueService {

    @Autowired
    private ClueRepository clueRepository;
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private MissionOptionRepository missionOptionRepository;
    @Autowired
    private ClueMapper clueMapper;

    public List<ClueDto> getClues() {
        final List<Clue> clues = clueRepository.findAll() == null ? new ArrayList<>() : clueRepository.findAll();
        final List<ClueDto> clueDtos = new ArrayList<>();
        if (!clues.isEmpty()) {
            clueDtos.addAll(clues.stream().map(p -> clueMapper.clueToClueDto(p)).toList());
        }
        return clueDtos;
    }

    public ClueDto getClueById(Integer id) {
        ClueDto clueDto;
        if (id != null) {
            Clue clue = clueRepository.findById(id)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Clues table", "The id " + id + " does not exist."));
            clueDto = clueMapper.clueToClueDto(clue);
        } else {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "id must be provided", "id is null");
        }

        return clueDto;
    }

    public ClueDto createClue(final ClueCreate clueCreate) {
        if (clueCreate == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Clue is null", "A body is required");
        }

        if (clueCreate.getContent() == null || clueCreate.getOrder() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Missing order or content of the clue", "Order and content of the clue need to be specified in the body");
        }
        if (clueCreate.getMissionId() == null && clueCreate.getMissionOptionId() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Missing missionId or missionOptionId", "The clue must be linked to a mission or a missionOption");
        }

        final Clue clue = new Clue();
        clue.setContent(clueCreate.getContent());
        clue.setOrder(clueCreate.getOrder());
        clue.setSubOrder(clueCreate.getSubOrder());

        final Integer missionId = clueCreate.getMissionId();
        if (missionId != null) {
            final Mission mission = missionRepository.findById(missionId).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission not found", "Mission with id " + missionId + " doesn't exist in the Missions database"));
            clue.setMission(mission);
        }

        final Integer missionOptionId = clueCreate.getMissionOptionId();
        if (missionOptionId != null) {
            final MissionOption missionOption = missionOptionRepository.findById(missionOptionId).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "MissionOption not found", "MissionOption with id " + missionOptionId + " doesn't exist in the Missions database"));
            clue.setMissionOption(missionOption);
        }

        clueRepository.save(clue);

        return clueMapper.clueToClueDto(clue);
    }

    public ClueDto updateClue(ClueDto clueDto) {
        if (clueDto == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The input clue is null", "The body is missing");
        } else if (clueDto.getId() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id is null", "The id must be provided");
        }

        log.info("RETREIVING CLUE ID = " + clueDto.getId());
        Clue existingClue = clueRepository.findById(clueDto.getId())
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Clue not found", "The id " + clueDto.getId() + " doesn't exist in the Clues database"));
        log.info("CLUE RETREIVED");

        if (clueDto.getOrder() != null) {
            existingClue.setOrder(clueDto.getOrder());
        }
        if (clueDto.getContent() != null) {
            existingClue.setContent(clueDto.getContent());
        }
        final Integer missionId = clueDto.getMission().getId();
        // missionId == null --> no modification
        if (missionId != null) {
            final Mission newStandardMission = missionRepository.findById(missionId)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission id " + missionId + " doesn't exist", "The mission was not found in the Missions database"));
            existingClue.setMission(newStandardMission);
        }

        clueRepository.save(existingClue);

        return clueMapper.clueToClueDto(existingClue);
    }

}
