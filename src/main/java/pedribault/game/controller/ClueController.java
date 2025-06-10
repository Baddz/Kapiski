package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateClue;
import pedribault.game.model.dto.ClueDto;
import pedribault.game.service.ClueService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/theGame/clues")
public class ClueController {

    private final ClueService clueService;

    @Autowired
    public ClueController(ClueService clueService) {
        this.clueService = clueService;
    }

    @GetMapping
    public ResponseEntity<List<ClueDto>> getClues() {
        try {
            log.info("[ClueController.getClues] START - Retrieving all clues");
            List<ClueDto> clues = clueService.getClues();
            if (clues.isEmpty()) {
                log.info("[ClueController.getClues] SUCCESS - No clues found in database");
            } else {
                log.info("[ClueController.getClues] SUCCESS - Retrieved {} clues", clues.size());
            }
            return ResponseEntity.ok(clues);
        } catch (Exception e) {
            log.error("[ClueController.getClues] ERROR - Failed to retrieve clues", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClueDto> getClueById(@PathVariable @NotNull Integer id) {
        try {
            log.info("[ClueController.getClueById] START - Retrieving clue with id {}", id);
            ClueDto clue = clueService.getClueById(id);
            log.info("[ClueController.getClueById] SUCCESS - Retrieved clue {}", clue.getId());
            return ResponseEntity.ok(clue);
        } catch (TheGameException e) {
            log.error("[ClueController.getClueById] ERROR - {} - {}", e.getMessage(), e.getDetailedMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[ClueController.getClueById] ERROR - Unexpected error retrieving clue {}", id, e);
            throw e;
        }
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<ClueDto>> getCluesByMission(@PathVariable @NotNull Integer missionId) {
        try {
            log.info("[ClueController.getCluesByMission] START - Retrieving clues for mission {}", missionId);
            List<ClueDto> clues = clueService.getCluesByMission(missionId);
            if (clues.isEmpty()) {
                log.info("[ClueController.getCluesByMission] SUCCESS - No clues found for mission {}", missionId);
            } else {
                log.info("[ClueController.getCluesByMission] SUCCESS - Retrieved {} clues for mission {}", clues.size(), missionId);
            }
            return ResponseEntity.ok(clues);
        } catch (TheGameException e) {
            log.error("[ClueController.getCluesByMission] ERROR - {} - {}", e.getMessage(), e.getDetailedMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[ClueController.getCluesByMission] ERROR - Unexpected error retrieving clues for mission {}", missionId, e);
            throw e;
        }
    }

    @GetMapping("/mission/{missionId}/order/{order}")
    public ResponseEntity<List<ClueDto>> getCluesByMissionAndOrder(
            @PathVariable @NotNull Integer missionId,
            @PathVariable @NotNull Integer order) {
        try {
            log.info("[ClueController.getCluesByMissionAndOrder] START - Retrieving clues for mission {} and order {}", missionId, order);
            List<ClueDto> clues = clueService.getCluesByMissionAndOrder(missionId, order);
            if (clues.isEmpty()) {
                log.info("[ClueController.getCluesByMissionAndOrder] SUCCESS - No clues found for mission {} and order {}", missionId, order);
            } else {
                log.info("[ClueController.getCluesByMissionAndOrder] SUCCESS - Retrieved {} clues", clues.size());
            }
            return ResponseEntity.ok(clues);
        } catch (TheGameException e) {
            log.error("[ClueController.getCluesByMissionAndOrder] ERROR - {} - {}", e.getMessage(), e.getDetailedMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[ClueController.getCluesByMissionAndOrder] ERROR - Unexpected error retrieving clues for mission {} and order {}", missionId, order, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<ClueDto> createClue(@RequestBody @Valid CreateOrUpdateClue clue) {
        try {
            log.info("[ClueController.createClue] START - Creating clue for mission {}", clue.getMissionId());
            ClueDto createdClue = clueService.createClue(clue);
            log.info("[ClueController.createClue] SUCCESS - Created clue {} for mission {}", createdClue.getId(), createdClue.getMission().getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClue);
        } catch (TheGameException e) {
            log.error("[ClueController.createClue] ERROR - {} - {}", e.getMessage(), e.getDetailedMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[ClueController.createClue] ERROR - Unexpected error creating clue", e);
            throw e;
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<ClueDto>> createClues(@RequestBody @Valid List<CreateOrUpdateClue> clues) {
        try {
            log.info("[ClueController.createClues] START - Creating {} clues", clues.size());
            List<ClueDto> createdClues = clueService.createClues(clues);
            log.info("[ClueController.createClues] SUCCESS - Created {} clues", createdClues.size());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClues);
        } catch (TheGameException e) {
            log.error("[ClueController.createClues] ERROR - {} - {}", e.getMessage(), e.getDetailedMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[ClueController.createClues] ERROR - Unexpected error creating clues", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClueDto> updateClue(
            @PathVariable @NotNull Integer id,
            @RequestBody @Valid CreateOrUpdateClue clue) {
        try {
            log.info("[ClueController.updateClue] START - Updating clue {}", id);
            ClueDto updatedClue = clueService.updateClue(id, clue);
            log.info("[ClueController.updateClue] SUCCESS - Updated clue {}", updatedClue.getId());
            return ResponseEntity.ok(updatedClue);
        } catch (TheGameException e) {
            log.error("[ClueController.updateClue] ERROR - {} - {}", e.getMessage(), e.getDetailedMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[ClueController.updateClue] ERROR - Unexpected error updating clue {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClue(@PathVariable @NotNull Integer id) {
        try {
            log.info("[ClueController.deleteClue] START - Deleting clue {}", id);
            clueService.deleteClue(id);
            log.info("[ClueController.deleteClue] SUCCESS - Deleted clue {}", id);
            return ResponseEntity.noContent().build();
        } catch (TheGameException e) {
            log.error("[ClueController.deleteClue] ERROR - {} - {}", e.getMessage(), e.getDetailedMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[ClueController.deleteClue] ERROR - Unexpected error deleting clue {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/mission/{missionId}")
    public ResponseEntity<Void> deleteCluesByMission(@PathVariable @NotNull Integer missionId) {
        try {
            log.info("[ClueController.deleteCluesByMission] START - Deleting clues for mission {}", missionId);
            clueService.deleteCluesByMission(missionId);
            log.info("[ClueController.deleteCluesByMission] SUCCESS - Deleted clues for mission {}", missionId);
            return ResponseEntity.noContent().build();
        } catch (TheGameException e) {
            log.error("[ClueController.deleteCluesByMission] ERROR - {} - {}", e.getMessage(), e.getDetailedMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[ClueController.deleteCluesByMission] ERROR - Unexpected error deleting clues for mission {}", missionId, e);
            throw e;
        }
    }
}
