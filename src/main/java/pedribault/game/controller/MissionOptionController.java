package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedribault.game.exceptions.GlobalExceptionHandler;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateMissionOption;
import pedribault.game.model.dto.CreateOrUpdate.UpdateMissionOption;
import pedribault.game.model.dto.MissionDto;
import pedribault.game.model.dto.MissionOptionDto;
import pedribault.game.service.MissionOptionService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/theGame/mission_option")
public class MissionOptionController {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;
    @Autowired
    private MissionOptionService missionOptionService;

    @GetMapping("/mission_options")
    public ResponseEntity<?> getMissionOptions() {
        try {
            log.info("[IN]=[GETTING MISSION_OPTIONS]");
            List<MissionOptionDto> missionOptionDtos = new ArrayList<>();

            missionOptionDtos = missionOptionService.getMissionOptions();
            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(missionOptionDtos, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<?> getMissionOptionsByMissionId(@PathVariable Integer missionId) {
        try {
            log.info("[IN]=[GETTING MISSION_OPTIONS [MISSION_ID]=[{}]]", missionId);
            List<MissionOptionDto> missionOptionDtos = new ArrayList<>();

            missionOptionDtos = missionOptionService.getMissionOptionsByMissionId(missionId);
            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(missionOptionDtos, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMissionOptionById(@PathVariable Integer id) {
        try {
            log.info("[IN]=[GETTING MISSION_OPTION [ID]=[{}]]", id);
            final MissionOptionDto missionOptionDto = missionOptionService.getMissionOptionsById(id);
            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(missionOptionDto, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @PostMapping("/mission/{missionId}/create")
    public ResponseEntity<?> createMissionOption(@RequestBody CreateOrUpdateMissionOption createOrUpdateMissionOption, @PathVariable Integer missionId) {
        try {
            log.info("[IN]=[CREATING MISSION_OPTION [MISSION_ID]=[{}]]", missionId);
            final MissionOptionDto missionOptionDto = missionOptionService.createMissionOption(createOrUpdateMissionOption, missionId);
            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(missionOptionDto, HttpStatus.CREATED);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    // TODO tests below

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateMissionOption(@PathVariable Integer id, @RequestParam Integer missionId, @RequestBody CreateOrUpdateMissionOption createOrUpdateMissionOption) {
        try {
            log.info("[IN]=[UPDATING MISSION_OPTION [ID]=[{}]]", id);
            final MissionOptionDto missionOptionDto = missionOptionService.updateMissionOption(id, missionId, createOrUpdateMissionOption);
            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(missionOptionDto, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{missionId}/mission_options")
    public ResponseEntity<?> updateMissionOptions(@PathVariable Integer missionId, @RequestBody List<UpdateMissionOption> createOrUpdateMissionOptions) {
        try {
            log.info("[IN]=[UPDATING MISSION_OPTIONS OF MISSION [ID]=[{}]]", missionId);
            final MissionDto missionDto = missionOptionService.updateMissionOptions(missionId, createOrUpdateMissionOptions);
            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(missionDto, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/{id}/clues/remove")
    public ResponseEntity<?> removeClues(@PathVariable Integer id, @RequestBody List<Integer> clueIds) {
        try {
            log.info("[IN]=[REMOVING CLUES FROM MISSION_OPTION [ID]=[{}]]", id);
            final MissionOptionDto missionOptionDto = missionOptionService.removeClues(id, clueIds);
            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(missionOptionDto, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @DeleteMapping("/mission/{missionId}/mission_option/{id}/remove")
    public ResponseEntity<?> removeMissionOption(@PathVariable Integer missionId, @PathVariable Integer id) {
        try {
            log.info("[IN]=[REMOVING CLUES FROM MISSION_OPTION [ID]=[{}]]", id);

        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }
}
