package pedribault.game.service;

import org.springframework.http.HttpStatus;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Escape;
import pedribault.game.model.Mission;
import pedribault.game.model.Universe;
import pedribault.game.repository.EscapeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pedribault.game.repository.UniverseRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class EscapeService {

    @Autowired
    private EscapeRepository escapeRepository;
    @Autowired
    private UniverseRepository universeRepository;

    public List<Escape> getEscapes() {
        return escapeRepository.findAll() == null ? new ArrayList<>() : escapeRepository.findAll();
    }

    public Escape getEscapeById(final Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The id can't be null", "The provided id is null.");
        }

        return escapeRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Escapes table", "The id " + id + " does not exist."));
    }

    public Escape createEscape(final Escape escape) {

        if (escape == null || escape.getTitle() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Title is null", "Title is required");
        }

        return escapeRepository.save(escape);
    }

    public Escape updateEscape(Integer id, Escape updatedEscape) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id not provided", "The id must be provided");
        }

        Escape existingEscape = escapeRepository.findById(id).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Escape not found", "The id " + id + "doesn't exist in the Escapes database"));

        if (updatedEscape == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "L'escape rentré est à null", "Le body doit être renseigné");
        }
        if (updatedEscape.getTitle() != null) {
            existingEscape.setTitle(updatedEscape.getTitle());
        }
        if (updatedEscape.getEscapePlayers() != null) {
            existingEscape.setEscapePlayers(updatedEscape.getEscapePlayers());
        }
        if (updatedEscape.getMissions() != null) {
            existingEscape.setMissions(updatedEscape.getMissions());
        }
        Integer updatedDifficulty = updatedEscape.getDifficulty();
        if (updatedDifficulty != null && existingEscape.getDifficulty() != updatedDifficulty) {
            existingEscape.setDifficulty(updatedDifficulty);
        }
        Double updatedSuccessRate = updatedEscape.getSuccessRate();
        if (updatedSuccessRate != null && existingEscape.getSuccessRate() != updatedSuccessRate) {
            existingEscape.setSuccessRate(updatedSuccessRate);
        }
        Universe updatedUniverse = updatedEscape.getUniverse();
        if (updatedUniverse != null) {
            Integer updatedUniverseId = updatedUniverse.getId();
            if (updatedUniverseId != null) {
                final Universe newUniverse = universeRepository.findById(updatedUniverseId).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Invalid Universe id", "In the body, please set the Universe's id to null, remove the Universe, or give a valid Universe id"));
                existingEscape.setUniverse(newUniverse);
            }
        }

        return escapeRepository.save(existingEscape);
    }
}

