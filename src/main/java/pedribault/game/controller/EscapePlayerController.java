package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedribault.game.exceptions.GlobalExceptionHandler;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateEscapePlayerMapping;
import pedribault.game.model.dto.EscapePlayerMappingDto;
import pedribault.game.service.EscapePlayerService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/theGame/escape_player")
public class EscapePlayerController {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;
    @Autowired
    private EscapePlayerService escapePlayerService;

    @GetMapping("/escape_players")
    public ResponseEntity<?> getEscapePlayerMappings() {
        try {
            log.info("[IN]=[GETTING ESCAPE_PLAYERS]");
            List<EscapePlayerMappingDto> escapePlayerMappingDtos = new ArrayList<>();
            escapePlayerMappingDtos = escapePlayerService.getEscapePlayerMappings();

            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(escapePlayerMappingDtos, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEscapePlayerMappingById(@PathVariable Integer id) {
        try {
            log.info("[IN]=[GETTING ESCAPE_PLAYER [ID]=[{}]]", id);
            final EscapePlayerMappingDto escapePlayerMappingDto = escapePlayerService.getEscapePlayerMappingById(id);

            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(escapePlayerMappingDto, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<?> getEscapePlayerMappingsByPlayerId(@PathVariable Integer playerId) {
        try {
            log.info("[IN]=[GETTING ESCAPE_PLAYERS [PLAYER_ID]=[{}]", playerId);
            List<EscapePlayerMappingDto> escapePlayerMappingDtos = new ArrayList<>();
            escapePlayerMappingDtos = escapePlayerService.getEscapePlayerMappingsByPlayerId(playerId);

            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(escapePlayerMappingDtos, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/escape/{escapeId}")
    public ResponseEntity<?> getEscapePlayerMappingsByEscapeId(@PathVariable Integer escapeId) {
        try {
            log.info("[IN]=[GETTING ESCAPE_PLAYERS [ESCAPE_ID]=[{}]", escapeId);
            List<EscapePlayerMappingDto> escapePlayerMappingDtos = new ArrayList<>();
            escapePlayerMappingDtos = escapePlayerService.getEscapePlayerMappingsByEscapeId(escapeId);

            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(escapePlayerMappingDtos, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/escape/{escapeId}/player/{playerId}")
    public ResponseEntity<?> getEscapePlayerMappingByEscapeIdAndPlayerId(@PathVariable Integer escapeId, @PathVariable Integer playerId) {
        try {
            log.info("[IN]=[GETTING ESCAPE_PLAYER [ESCAPE_ID]=[{}], [PLAYER_ID]=[{}]]", escapeId, playerId);
            final EscapePlayerMappingDto escapePlayerMappingDto = escapePlayerService.getEscapePlayerMappingsByEscapeIdAndPlayerId(escapeId, playerId);

            final StringBuilder reqOut = new StringBuilder();
            reqOut.append("[OUT]=[[STATUS]=[OK]]");
            log.info(reqOut.toString());

            return new ResponseEntity<>(escapePlayerMappingDto, HttpStatus.OK);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }

    @GetMapping("/create")
    public ResponseEntity<?> createEscapePlayerMapping(@RequestBody CreateOrUpdateEscapePlayerMapping createOrUpdateEscapePlayerMapping) {
        log.info("[IN]=[CREATING ESCAPE_PLAYER]");
        try {
            EscapePlayerMappingDto escapePlayerMappingDto = escapePlayerService.createEscapePlayerMapping(createOrUpdateEscapePlayerMapping);
            return new ResponseEntity<>(escapePlayerMappingDto, HttpStatus.CREATED);
        } catch (Exception e) {
            return globalExceptionHandler.handleException(e);
        }
    }
}
