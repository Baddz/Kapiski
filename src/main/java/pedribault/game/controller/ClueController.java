package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedribault.game.exceptions.ErrorResponse;
import pedribault.game.exceptions.GlobalExceptionHandler;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateClue;
import pedribault.game.model.dto.ClueDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.service.ClueService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/theGame/clue")
public class ClueController {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private final ClueService clueService;

    @Autowired
    public ClueController(ClueService clueService) {
        this.clueService = clueService;
    }

    @GetMapping("/clues")
    public ResponseEntity<?> getClues() {

        try {
            log.info("[IN]=[GETTING CLUES]");
            List<ClueDto> clues = new ArrayList<>();

            clues = clueService.getClues();
            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(clues, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClueById(@PathVariable Integer id) {

        try {
            log.info("[IN]=[GETTING CLUE [ID]=[{}]]", id);
            final ClueDto clue = clueService.getClueById(id);

            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(clue, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }

    }

    @PostMapping("/create")
    public ResponseEntity<?> createClue(@RequestBody CreateOrUpdateClue clue) {

        log.info("[IN]=[CREATING CLUE]");
        try {
            ClueDto createdClue = clueService.createClue(clue);
            log.info("[OUT]=[[STATUS]=[CREATED],[CLUE_ID]=[{}],[MISSION_ID]=[{}],[ORDER]=[{}],[SUB_ORDER]=[{}],[CONTENT]=[{}], ]", createdClue.getId(), createdClue.getMission().getId(), createdClue.getOrder(), createdClue.getSubOrder(), createdClue.getContent());
            return new ResponseEntity<>(createdClue, HttpStatus.CREATED);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateClue(@PathVariable Integer id, @RequestBody CreateOrUpdateClue createOrUpdateClue) {
        log.info("[IN]=[UPDATING CLUE]");
        try {
            ClueDto clue = clueService.updateClue(id, createOrUpdateClue);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(clue, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }
}
