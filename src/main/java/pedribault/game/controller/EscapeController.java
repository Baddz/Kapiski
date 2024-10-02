package pedribault.game.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedribault.game.exceptions.ErrorResponse;
import pedribault.game.exceptions.GlobalExceptionHandler;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateEscape;
import pedribault.game.model.dto.EscapeDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.service.EscapeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/theGame/escape")
public class EscapeController {

    private final GlobalExceptionHandler globalExceptionHandler;
    private final EscapeService escapeService;

    @Autowired
    public EscapeController(final GlobalExceptionHandler globalExceptionHandler, EscapeService escapeService) {
        this.globalExceptionHandler = globalExceptionHandler;
        this.escapeService = escapeService;
    }

    @GetMapping("/escapes")
    public ResponseEntity<?> getEscapes() {

        try {
            log.info("[IN]=[GETTING ESCAPES]");
            List<EscapeDto> escapes = new ArrayList<>();

            escapes = escapeService.getEscapes();
            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(escapes, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEscapeById(@PathVariable Integer id) {

        try {
        log.info("[IN]=[GETTING UNIVERSE [ID]=[{}]]", id);
        EscapeDto escape = escapeService.getEscapeById(id);

        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return new ResponseEntity<>(escape, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> createEscape(@RequestBody CreateOrUpdateEscape escape) {

        log.info("[IN]=[CREATING ESCAPE]");
        try {
            EscapeDto createdEscape = escapeService.createEscape(escape);
            log.info("[OUT]=[[STATUS]=[CREATED],[TITLE]=[{}],[SUCCESS_RATE]=[{}],[DIFFICULTY]=[{}]]", escape.getTitle(), escape.getSuccessRate(), escape.getDifficulty());
            return new ResponseEntity<>(createdEscape, HttpStatus.CREATED);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateEscape(@RequestBody CreateOrUpdateEscape createOrUpdateEscape, @PathVariable Integer id) {
        log.info("[IN]=[UPDATING ESCAPE");
        try {
            EscapeDto escape = escapeService.updateEscape(createOrUpdateEscape, id);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(escape, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }
}
