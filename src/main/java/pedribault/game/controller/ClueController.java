package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Clue;
import pedribault.game.service.ClueService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/theGame/clue")
public class ClueController {

    private ClueService clueService;

    @Autowired
    public ClueController(ClueService clueService) {
        this.clueService = clueService;
    }

    @GetMapping("/clues")
    public List<Clue> getClues() {

        log.info("[IN]=[GETTING CLUES]");
        List<Clue> clues = new ArrayList<>();

        clues = clueService.getClues();
        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return clues;

    }

    @GetMapping("/{id}")
    public Clue getClueById(@PathVariable Integer id) {

        log.info("[IN]=[GETTING CLUE [ID]=[{}]]", id);
        Clue clue = null;
        clue = clueService.getClueById(id);

        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return clue;

    }

    @PostMapping("/create")
    public ResponseEntity<Clue> createClue(@RequestBody Clue clue) {

        log.info("[IN]=[CREATING CLUE]");
        try {
            Clue createdClue = clueService.createClue(clue);
            log.info("[OUT]=[[STATUS]=[CREATED],[MISSION_ID]=[{}],[CONTENT]=[{}]]", clue.getMission().getId(), clue.getContent());
            return new ResponseEntity<>(createdClue, HttpStatus.CREATED);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]",
                    e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Clue> updateClue(@PathVariable Integer id, @RequestBody Clue updatedClue) {
        log.info("[IN]=[UPDATING CLUE [ID]=[{}]]", id);
        try {
            Clue clue = clueService.updateClue(id, updatedClue);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(clue, HttpStatus.OK);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]]", e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

}
