package pedribault.game.service;

import org.springframework.http.HttpStatus;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Escape;
import pedribault.game.model.EscapePlayer;
import pedribault.game.model.Mission;
import pedribault.game.model.PlayerMission;
import pedribault.game.repository.EscapeRepository;
import pedribault.game.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private PlayerMissionService playerMissionService;
    @Autowired
    private EscapeRepository escapeRepository;

    public List<Mission> getMissions() {
        return missionRepository.findAll() == null ? new ArrayList<>() : missionRepository.findAll();
    }

    public Mission getMissionById(final Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The id can't be null", "The provided id is null.");
        }

        return missionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Missions table", "The id " + id + " does not exist."));
    }

    public Mission createMission(Mission mission) {
        if (mission == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Mission is null", "A body is required");
        } else if (mission.getTitle() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Title is null", "Title is required");
        } else if (mission.getEscape() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Reference escape is null", "A mission needs to refer to an Escape with a valid id");
        } else if (mission.getEscape().getId() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Escape id is null", "A mission needs to refer to an Escape with a valid id");
        }

        if (mission.getVisible() == null) {
            mission.setVisible(true);
        }

        if (mission.getOptional() == null) {
            mission.setOptional(false);
        }

        if (mission.getMissionOrder() == null) {
            mission.setMissionOrder(0);
        }

        return missionRepository.save(mission);
    }

    public Mission updateMission(Integer id, Mission updatedMission) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id not provided", "The id must be provided");
        }
        if (updatedMission == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The input mission is null", "The body is missing");
        }

        Mission existingMission = missionRepository.findById(id).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Universe not found", "The id " + id + "doesn't exist in the Escapes database"));

        if (updatedMission.getTitle() != null) {
            existingMission.setTitle(updatedMission.getTitle());
        }
        if (updatedMission.getVisible() != null) {
            existingMission.setVisible(updatedMission.getVisible());
        }
        if (updatedMission.getOptional() != null) {
            existingMission.setOptional(updatedMission.getOptional());
        }
        if (updatedMission.getMissionOrder() != null) {
            existingMission.setMissionOrder(updatedMission.getMissionOrder());
        }
        if (updatedMission.getSuccessRate() != null) {
            existingMission.setSuccessRate(updatedMission.getSuccessRate());
        }
        if (updatedMission.getEscape() != null) {
            Integer updatedEscapeId = updatedMission.getEscape().getId();
            if (updatedEscapeId != null) {
                Escape updatedMissionEscape = escapeRepository.findById(updatedEscapeId)
                        .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission not found", "Mission with id " + updatedEscapeId + " was not found"));
                existingMission.setEscape(updatedMissionEscape);
            }
        }

        return missionRepository.save(existingMission);
    }

}
