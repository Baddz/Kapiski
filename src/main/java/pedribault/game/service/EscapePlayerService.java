package pedribault.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.EscapePlayer;
import pedribault.game.repository.EscapePlayerRepository;

@Service
public class EscapePlayerService {

    @Autowired
    private EscapePlayerRepository escapePlayerRepository;

    public EscapePlayer findEscapePlayerByEscapeIdAndPlayerId(Integer escapeId, Integer playerId) {
        return escapePlayerRepository.findByEscapeIdAndPlayerId(escapeId, playerId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "EscapePlayer not found",
                        "EscapePlayer with escapeId " + escapeId + " and playerId " + playerId + " not found."));
    }
}
