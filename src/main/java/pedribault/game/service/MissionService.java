package pedribault.game.service;

import org.springframework.http.HttpStatus;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Escape;
import pedribault.game.model.StandardMission;
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

    public List<StandardMission> getMissions() {
        return missionRepository.findAll() == null ? new ArrayList<>() : missionRepository.findAll();
    }

    public StandardMission getMissionById(final Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The id can't be null", "The provided id is null.");
        }

        return missionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Missions table", "The id " + id + " does not exist."));
    }

    public StandardMission createMission(StandardMission standardMission) {
        if (standardMission == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Mission is null", "A body is required");
        } else if (standardMission.getTitle() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Title is null", "Title is required");
        } else if (standardMission.getEscape() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Reference escape is null", "A mission needs to refer to an Escape with a valid id");
        } else if (standardMission.getEscape().getId() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Escape id is null", "A mission needs to refer to an Escape with a valid id");
        }

        if (standardMission.getVisible() == null) {
            standardMission.setVisible(true);
        }

        if (standardMission.getOptional() == null) {
            standardMission.setOptional(false);
        }

        if (standardMission.getMissionOrder() == null) {
            standardMission.setMissionOrder(0);
        }

        return missionRepository.save(standardMission);
    }

    public StandardMission updateMission(Integer id, StandardMission updatedStandardMission) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id not provided", "The id must be provided");
        }
        if (updatedStandardMission == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The input mission is null", "The body is missing");
        }

        StandardMission existingStandardMission = missionRepository.findById(id).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Universe not found", "The id " + id + "doesn't exist in the Escapes database"));

        if (updatedStandardMission.getTitle() != null) {
            existingStandardMission.setTitle(updatedStandardMission.getTitle());
        }
        if (updatedStandardMission.getVisible() != null) {
            existingStandardMission.setVisible(updatedStandardMission.getVisible());
        }
        if (updatedStandardMission.getOptional() != null) {
            existingStandardMission.setOptional(updatedStandardMission.getOptional());
        }
        if (updatedStandardMission.getMissionOrder() != null) {
            existingStandardMission.setMissionOrder(updatedStandardMission.getMissionOrder());
        }
        if (updatedStandardMission.getSuccessRate() != null) {
            existingStandardMission.setSuccessRate(updatedStandardMission.getSuccessRate());
        }
        if (updatedStandardMission.getEscape() != null) {
            Integer updatedEscapeId = updatedStandardMission.getEscape().getId();
            if (updatedEscapeId != null) {
                Escape updatedMissionEscape = escapeRepository.findById(updatedEscapeId)
                        .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission not found", "Mission with id " + updatedEscapeId + " was not found"));
                existingStandardMission.setEscape(updatedMissionEscape);
            }
        }

        return missionRepository.save(existingStandardMission);
    }

}
