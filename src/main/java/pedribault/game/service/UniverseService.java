package pedribault.game.service;

import org.springframework.http.HttpStatus;
import pedribault.game.model.dto.UniverseDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Universe;
import pedribault.game.repository.UniverseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UniverseService {

    @Autowired
    private UniverseRepository universeRepository;

    public List<Universe> getUniverses() {
        return universeRepository.findAll() == null ? new ArrayList<>() : universeRepository.findAll();
    }

    public Universe getUniverseById(Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The id can't be null", "The provided id is null.");
        }

        return universeRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Universes table", "The id " + id + " does not exist."));
    }
//TODO
    public UniverseDto createUniverse(final Universe universe) {

        if (universe == null || universe.getTitle() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Title is null", "Title is required");
        }

        universeRepository.save(universe);

        return null;

    }

    public Universe updateUniverse(Integer id, Universe updatedUniverse) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id not provided", "The id must be provided");
        }
        Universe existingUniverse = universeRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Universe not found", "The id " + id + "doesn't exist in the Universes database"));

        if (updatedUniverse.getTitle() != null) {
            existingUniverse.setTitle(updatedUniverse.getTitle());
        }

        return universeRepository.save(existingUniverse);
    }
}
