package pedribault.game.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedribault.game.dto.UniverseDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Universe;
import pedribault.game.service.UniverseService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/theGame/universe")
public class UniverseController {

    private UniverseService universeService;
    @Autowired
    public UniverseController(UniverseService universeService) {
        this.universeService = universeService;
    }

    @GetMapping("/universes")
    public List<Universe> getUniverses() {

        log.info("[IN]=[GETTING UNIVERSES]");
        List<Universe> universes = new ArrayList<>();

        universes = universeService.getUniverses();
        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]:[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return universes;
    }

    @GetMapping("/{id}")
    public Universe getUniverseById(@PathVariable Integer id) {

        log.info("[IN]=[GETTING UNIVERSE [ID]=[{}]]", id);
        Universe universe = universeService.getUniverseById(id);

        final StringBuilder reqOut = new StringBuilder();
        reqOut.append("[OUT]=[[STATUS]=[OK]]");
        log.info(reqOut.toString());

        return universe;
    }

    @PostMapping("/create")
    public ResponseEntity<UniverseDto> createUniverse(@RequestBody Universe universe) {

        StringBuilder reqIn = new StringBuilder();
        reqIn.append("[IN]=[CREATING UNIVERSE]");
        log.info(reqIn.toString());

        try {
            UniverseDto createdUniverse = universeService.createUniverse(universe);
            log.info("[OUT]=[[STATUS]=[CREATED],[TITLE]=[{}]]", universe.getTitle());
            return new ResponseEntity<>(createdUniverse, HttpStatus.CREATED);
        } catch (TheGameException e) {
            log.error("[ERROR]=[{}]", e.getMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Universe> updateUniverse(@PathVariable Integer id, @RequestBody Universe updatedUniverse) {
        log.info("[IN]=[UPDATING UNIVERSE [ID]=[{}]]", id);
        try {
            Universe universe = universeService.updateUniverse(id, updatedUniverse);
            log.info("[OUT]=[[STATUS]=[OK]]");
            return new ResponseEntity<>(universe, HttpStatus.OK);
        } catch (TheGameException e) {
            log.error("[OUT]=[[STATUS]=[KO],[ERROR]=[[STATUS]=[{}],[MESSAGE]=[{}],[DETAILED_MESSAGE]=[{}]]]", e.getStatus(), e.getMessage(), e.getDetailedMessage());
            return new ResponseEntity<>(null, e.getStatus());
        }
    }

}
