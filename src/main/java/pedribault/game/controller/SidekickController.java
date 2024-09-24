package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedribault.game.model.dto.SidekickDto;
import pedribault.game.model.dto.CreateOrUpdate.SidekickUpdate;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.service.SidekickService;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/theGame/sidekick")
public class SidekickController {

    @Autowired
    private SidekickService sidekickService;
    @Autowired
    public SidekickController(SidekickService sidekickService) {
        this.sidekickService = sidekickService;
    }

    @GetMapping("/sidekicks")
    public List<SidekickDto> getSidekicks() {

        log.info("[IN]=[GETTING SIDEKICKS]");
        List<SidekickDto> sidekicks = sidekickService.getSidekicks();
        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return sidekicks;
    }

    @GetMapping("/get")
    public SidekickDto getSidekick(@RequestParam(required = false) Integer id,
                            @RequestParam(required = false) String name,
                            @RequestParam(required = false) String firstName) {

        log.info("[IN]=[GETTING SIDEKICK [ID]=[{}], [NAME]=[{}], [FIRSTNAME]=[{}]]", id, name, firstName);
        SidekickDto sidekick = sidekickService.getSidekick(id, name, firstName);

        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return sidekick;
    }

    @PostMapping("/create")
    public ResponseEntity<SidekickDto> createSidekick(@RequestBody SidekickDto sidekick) {

        log.info("[IN]=[CREATING SIDEKICK]");
        try {
            SidekickDto createdSidekick = sidekickService.createSidekick(sidekick);
            log.info("[OUT]=[[STATUS]=[CREATED],[NAME]=[{}],[FIRST_NAME]=[{}],[MAIL]=[{}],[ADDRESS]=[{}]]",
                    sidekick.getName(), sidekick.getFirstName(), sidekick.getMail(), sidekick.getAddress());
            return new ResponseEntity<>(createdSidekick, HttpStatus.CREATED);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]",
                    e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<SidekickDto> updateSidekick(@RequestBody SidekickUpdate sidekickUpdate) {
        log.info("[IN]=[UPDATING SIDEKICK]");
        try {
            SidekickDto sidekick = sidekickService.updateSidekick(sidekickUpdate);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(sidekick, HttpStatus.OK);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]]", e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @PutMapping("/{id}/players/add")
    public ResponseEntity<SidekickDto> addPlayer(@PathVariable Integer id, @RequestBody List<Integer> playerIds) {
        log.info("[IN]=[UPDATING SIDEKICK - ADDING PLAYERS]");
        try {
            SidekickDto sidekick = sidekickService.addPlayers(id, playerIds);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(sidekick, HttpStatus.OK);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]]", e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @PutMapping("/{id}/players/delete")
    public ResponseEntity<SidekickDto> removePlayer(@PathVariable Integer id, @RequestBody List<Integer> playerIds) {
        log.info("[IN]=[UPDATING SIDEKICK - ADDING PLAYERS]");
        try {
            SidekickDto sidekick = sidekickService.removePlayers(id, playerIds);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(sidekick, HttpStatus.OK);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]]", e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @PutMapping("/{id}/players/update")
    public ResponseEntity<SidekickDto> updatePlayer(@PathVariable Integer id, @RequestBody List<Integer> playerIds) {
        log.info("[IN]=[UPDATING SIDEKICK - ADDING PLAYERS]");
        try {
            SidekickDto sidekick = sidekickService.updatePlayers(id, playerIds);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(sidekick, HttpStatus.OK);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]]", e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }
}
