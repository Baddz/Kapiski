package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pedribault.game.exceptions.GlobalExceptionHandler;
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
}
