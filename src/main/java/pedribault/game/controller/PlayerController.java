package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedribault.game.dto.PlayerDto;
import pedribault.game.dto.PlayerUpdate;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Player;
import pedribault.game.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/theGame/player")
public class PlayerController {

    private PlayerService playerService;
    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    public List<PlayerDto> getPlayers() {

        log.info("[IN]=[GETTING PLAYERS]");
        List<PlayerDto> players = new ArrayList<>();

        players = playerService.getPlayers();
        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return players;
    }

    @GetMapping("/get")
    public PlayerDto getPlayer(@RequestParam(required = false) Integer id,
                            @RequestParam(required = false) String name,
                            @RequestParam(required = false) String firstName) {

        log.info("[IN]=[GETTING PLAYER [ID]=[{}], [NAME]=[{}], [FIRSTNAME]=[{}]]", id, name, firstName);
        PlayerDto player = playerService.getPlayer(id, name, firstName);

        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return player;
    }

    @PostMapping("/create")
    public ResponseEntity<PlayerDto> createPlayer(@RequestBody PlayerDto player) {

        log.info("[IN]=[CREATING PLAYER]");
        try {
            PlayerDto createdPlayer = playerService.createPlayer(player);
            log.info("[OUT]=[[STATUS]=[CREATED],[NAME]=[{}],[FIRST_NAME]=[{}],[MAIL]=[{}],[ADDRESS]=[{}]]",
                    player.getName(), player.getFirstName(), player.getMail(), player.getAddress());
            return new ResponseEntity<>(createdPlayer, HttpStatus.CREATED);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]",
                    e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<PlayerDto> updatePlayer(@RequestBody PlayerUpdate playerUpdate) {
        log.info("[IN]=[UPDATING PLAYER]");
        try {
            PlayerDto player = playerService.updatePlayer(playerUpdate);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(player, HttpStatus.OK);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]]", e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }
}
