package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Escape;
import pedribault.game.model.Mission;
import pedribault.game.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/theGame/mission")
public class MissionController {

    private MissionService missionService;
    @Autowired
    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @GetMapping("/missions")
    public List<Mission> getMissions() {

        log.info("[IN]=[GETTING MISSIONS]");
        List<Mission> missions = new ArrayList<>();

        missions = missionService.getMissions();
        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return missions;
    }

    @GetMapping("/{id}")
    public Mission getMissionById(@PathVariable Integer id) {

        log.info("[IN]=[GETTING MISSION [ID]=[{}]]", id);
        Mission mission = missionService.getMissionById(id);

        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return mission;
    }

    @PostMapping("/create")
    public ResponseEntity<Mission> createMission(@RequestBody Mission mission) {

        log.info("[IN]=[CREATING MISSION]");
        try {
            Mission createdMission = missionService.createMission(mission);
            log.info("[OUT]=[[STATUS]=[CREATED],[TITLE]=[{}],[VISIBLE]=[{}],[ESCAPE_ID]=[{}],[ORDER]=[{}],[SUCCESS_RATE]=[{}],[OPTIONAL]=[{}]]",
                    mission.getTitle(), mission.getVisible(), mission.getEscape().getId(), mission.getMissionOrder(), mission.getSuccessRate(), mission.getOptional());
            return new ResponseEntity<>(createdMission, HttpStatus.CREATED);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]",
                    e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Mission> updateMission(@PathVariable Integer id, @RequestBody Mission updatedMission) {
        log.info("[IN]=[UPDATING MISSION [ID]=[{}]]", id);
        try {
            Mission mission = missionService.updateMission(id, updatedMission);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(mission, HttpStatus.OK);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]]", e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }
}
