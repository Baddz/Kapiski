package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pedribault.game.exceptions.GlobalExceptionHandler;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Mission;
import pedribault.game.model.StandardMission;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateMission;
import pedribault.game.model.dto.MissionDto;
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
            List<MissionDto> missions = new ArrayList<>();

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
    public ResponseEntity<?> getMissionById(@PathVariable Integer id) {

        try {
            log.info("[IN]=[GETTING MISSION [ID]=[{}]]", id);
            MissionDto mission = missionService.getMissionById(id);

            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(mission, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMission(@RequestBody CreateOrUpdateMission mission) {

        log.info("[IN]=[CREATING MISSION]");
        try {
            MissionDto createdMission = missionService.createMission(mission);
            log.info("[OUT]=[[STATUS]=[CREATED],[TYPE]=[{}],[TITLE]=[{}],[DESCRIPTION]=[{}],[ORDER]=[{}],[VISIBLE]=[{}],[OPTIONAL]=[{}],[ESCAPE_ID]=[{}],[SUCCESS_RATE]=[{}],[SUB_ORDER]=[{}]]",
                    mission.getMissionType(), mission.getTitle(), mission.getDescription(), mission.getOrder(), mission.getIsVisible(),mission.getIsOptional(), mission.getEscapeId(), mission.getSuccessRate(), mission.getSubOrder());
            return new ResponseEntity<>(createdMission, HttpStatus.CREATED);
        } catch (TheGameException e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateMission(@PathVariable Integer id, @RequestBody CreateOrUpdateMission createOrUpdateMission) {
        log.info("[IN]=[UPDATING MISSION [ID]=[{}]]", id);
        try {
            MissionDto mission = missionService.updateMission(id, createOrUpdateMission);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(mission, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }
}
