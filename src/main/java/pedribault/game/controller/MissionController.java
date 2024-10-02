package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pedribault.game.exceptions.GlobalExceptionHandler;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Mission;
import pedribault.game.model.StandardMission;
import pedribault.game.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/theGame/mission")
public class MissionController {

    private final GlobalExceptionHandler globalExceptionHandler;
    private final MissionService missionService;
    @Autowired
    public MissionController(final GlobalExceptionHandler globalExceptionHandler, MissionService missionService) {
        this.globalExceptionHandler = globalExceptionHandler;
        this.missionService = missionService;
    }

    @GetMapping("/missions")
    public ResponseEntity<?> getMissions() {

        try {
            log.info("[IN]=[GETTING MISSIONS]");
            List<Mission> missions = new ArrayList<>();

            missions = missionService.getMissions();
            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(missions, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    public Mission getMissionById(@PathVariable Integer id) {

        log.info("[IN]=[GETTING MISSION [ID]=[{}]]", id);
        Mission standardMission = missionService.getMissionById(id);

        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return standardMission;
    }

    @PostMapping("/create")
    public ResponseEntity<Mission> createMission(@RequestBody StandardMission standardMission) {

        log.info("[IN]=[CREATING MISSION]");
        try {
            Mission createdStandardMission = missionService.createMission(standardMission);
            log.info("[OUT]=[[STATUS]=[CREATED],[TITLE]=[{}],[VISIBLE]=[{}],[ESCAPE_ID]=[{}],[ORDER]=[{}],[SUCCESS_RATE]=[{}],[OPTIONAL]=[{}]]",
                    standardMission.getTitle(), standardMission.getIsVisible(), standardMission.getEscape().getId(), standardMission.getOrder(), standardMission.getSuccessRate(), standardMission.getIsOptional());
            return new ResponseEntity<>(createdStandardMission, HttpStatus.CREATED);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]",
                    e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Mission> updateMission(@PathVariable Integer id, @RequestBody StandardMission updatedStandardMission) {
        log.info("[IN]=[UPDATING MISSION [ID]=[{}]]", id);
        try {
            Mission standardMission = missionService.updateMission(id, updatedStandardMission);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(standardMission, HttpStatus.OK);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]]", e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }
}
