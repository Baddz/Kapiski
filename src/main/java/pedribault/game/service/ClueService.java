package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pedribault.game.dto.ClueDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.ClueMapper;
import pedribault.game.model.Clue;
import pedribault.game.model.Mission;
import pedribault.game.repository.ClueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pedribault.game.repository.MissionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ClueService {

    @Autowired
    private ClueRepository clueRepository;
    @Autowired
    private MissionRepository missionRepository;
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

    public ClueDto createClue(final ClueDto clueDto) {
        if (clueDto == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Clue is null", "A body is required");
        }

        final Clue clue = clueMapper.clueDtoToClue(clueDto);

        clueRepository.save(clue);

        return clueDto;
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
        if (clueDto.getMissionId() != null) {
            updateMission(clueDto.getId(), clueDto.getMissionId());
        }

        clueRepository.save(existingClue);

        return clueMapper.clueToClueDto(existingClue);
    }

    public ClueDto updateMission(Integer id, Integer missionId) {

        final Clue clue = clueRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Clue id " + id + " doesn't exist", "The clue was not found in the Clues database"));
        final Mission currentMission = clue.getMission();
        if (missionId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The mission id can't be null", "Enter a valid mission id");
        }
        final Mission newMission = missionRepository.findById(missionId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission id " + missionId + " doesn't exist", "The mission was not found in the Missions database"));
        clue.setMission(newMission);
        if (!Objects.equals(currentMission, newMission)) {
            if (currentMission != null) {
                currentMission.removeClue(clue);
                missionRepository.save(currentMission);
            }
            newMission.addClue(clue);
            missionRepository.save(newMission);
            clueRepository.save(clue);
        }
        final ClueDto clueDto = clueMapper.clueToClueDto(clue);
        return clueDto;
    }
}
