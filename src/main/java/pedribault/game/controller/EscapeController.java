package pedribault.game.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Escape;
import pedribault.game.service.EscapeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/theGame/escape")
public class EscapeController {

    private EscapeService escapeService;
    @Autowired
    public EscapeController(EscapeService escapeService) {
        this.escapeService = escapeService;
    }

    @GetMapping("/escapes")
    public List<Escape> getEscapes() {

        log.info("[IN]=[GETTING ESCAPES]");
        List<Escape> escapes = new ArrayList<>();

        escapes = escapeService.getEscapes();
        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return escapes;
    }

    @GetMapping("/{id}")
    public Escape getEscapeById(@PathVariable Integer id) {

        log.info("[IN]=[GETTING UNIVERSE [ID]=[{}]]", id);
        Escape escape = escapeService.getEscapeById(id);

        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return escape;
    }


    @PostMapping("/create")
    public ResponseEntity<Escape> createEscape(@RequestBody Escape escape) {

        log.info("[IN]=[CREATING ESCAPE]");
        try {
            Escape createdEscape = escapeService.createEscape(escape);
            log.info("[OUT]=[[STATUS]=[CREATED],[TITLE]=[{}],[SUCCESS_RATE]=[{}],[DIFFICULTY]=[{}],[UNIVERSE_ID]=[{}]]", escape.getTitle(), escape.getSuccessRate(), escape.getDifficulty(), escape.getUniverse().getId());
            return new ResponseEntity<>(createdEscape, HttpStatus.CREATED);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]",
                    e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Escape> updateEscape(@PathVariable Integer id, @RequestBody Escape updatedEscape) {
        log.info("[IN]=[UPDATING ESCAPE [ID]=[{}]]", id);
        try {
            Escape escape = escapeService.updateEscape(id, updatedEscape);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(escape, HttpStatus.OK);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]]", e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }
}
