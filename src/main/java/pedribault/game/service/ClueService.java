package pedribault.game.service;

import org.springframework.http.HttpStatus;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Clue;
import pedribault.game.model.Mission;
import pedribault.game.model.Universe;
import pedribault.game.repository.ClueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pedribault.game.repository.MissionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClueService {

    @Autowired
    private ClueRepository clueRepository;
    @Autowired
    private MissionRepository missionRepository;

    public List<Clue> getClues() {
        return clueRepository.findAll() == null ? new ArrayList<>() : clueRepository.findAll();
    }

    public Clue getClueById(Integer id) {

        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The id can't be null", "The provided id is null.");
        }

        return clueRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Clues table", "The id " + id + " does not exist."));
    }

    public Clue createClue(final Clue clue) {

        Clue newClue;
        if (clue == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The Clue is null", "A body is required");
        } else if (clue.getContent() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The content of the Clue is null", "A content is required for clues");
        } else if (clue.getMission() == null || clue.getMission().getId() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The Mission id is null", "A mission with a valid id is required in the body");
        } else {
            newClue = new Clue();
            final Mission mission = missionRepository.findById(clue.getMission().getId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Invalid Mission id", "In the body, please give a valid Mission id"));
            newClue.setMission(mission);
        }

        if (clue.getOrder() == null) {
            newClue.setOrder(0);
        } else {
            newClue.setOrder(clue.getOrder());
        }

        newClue.setContent(clue.getContent());

        return clueRepository.save(newClue);
    }

    public Clue updateClue(Integer id, Clue updatedClue) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id not provided", "The id must be provided");
        }
        Clue existingClue = clueRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Clue not found", "The id " + id + "doesn't exist in the Clues database"));

        Mission updatedMission = updatedClue.getMission();
        if (updatedMission != null) {
            Integer updatedMissionId = updatedMission.getId();
            if (updatedMissionId != null) {
                final Mission newMission = missionRepository.findById(updatedMissionId).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Invalid Mission id", "In the body, please set the Mission's id to null, remove the Mission, or give a valid Mission id"));
                existingClue.setMission(newMission);
            }
        }

        if (updatedClue.getOrder() != null) {
            existingClue.setOrder(updatedClue.getOrder());
        }
        if (updatedClue.getContent() != null) {
            existingClue.setContent(updatedClue.getContent());
        }

        return clueRepository.save(existingClue);
    }
}
