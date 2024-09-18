package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pedribault.game.dto.PlayerStatus;
import pedribault.game.model.EscapePlayer;
import pedribault.game.repository.EscapePlayerRepository;

@Component
public class EscapePlayerMapper {

    @Autowired
    private EscapePlayerRepository escapePlayerRepository;

    public PlayerStatus escapePlayerToEscapePlayerDto(EscapePlayer escapePlayer) {
        final PlayerStatus playerStatus = new PlayerStatus();
        playerStatus.setPlayerId(escapePlayer.getIdPlayer());
        playerStatus.setStatus(escapePlayer.getStatus());
        return playerStatus;
    }
}
