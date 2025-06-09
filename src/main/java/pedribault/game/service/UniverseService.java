package pedribault.game.service;

import org.springframework.http.HttpStatus;
import pedribault.game.mappers.UniverseMapper;
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

    @Autowired
    private UniverseMapper universeMapper;

    public List<UniverseDto> getUniverses() {
        final List<Universe> universes = universeRepository.findAll() == null ? new ArrayList<>() : universeRepository.findAll();
        return universes.stream()
                .map(universeMapper::universeToUniversDTO)
                .toList();
    }

    public Universe getUniverseById(Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "[getUniverseById] missing id", "universe_id must be provided");
        }

        return universeRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "[getUniverseById] Universe not found", "universe_id " + id + " does not exist"));
    }
//TODO
    public UniverseDto createUniverse(final Universe universe) {
        if (universe == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "[createUniverse] Missing payload", "Universe must be provided");
        }
        if (universe.getTitle() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "[createUniverse] Missing title", "Universe title is required");
        }

        universeRepository.save(universe);
        return universeMapper.universeToUniversDTO(universe);
    }

    public Universe updateUniverse(Integer id, Universe updatedUniverse) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "[updateUniverse] Missing id", "universe_id must be provided");
        }
        if (updatedUniverse == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "[updateUniverse] Missing payload", "updatedUniverse must be provided");
        }

        Universe existingUniverse = universeRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "[updateUniverse] Universe not found", "universe_id " + id + " does not exist"));

        if (updatedUniverse.getTitle() != null) {
            existingUniverse.setTitle(updatedUniverse.getTitle());
        }

        return universeRepository.save(existingUniverse);
    }
}
