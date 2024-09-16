package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pedribault.game.dto.EscapePlayerDto;
import pedribault.game.model.EscapePlayer;
import pedribault.game.repository.EscapePlayerRepository;

@Component
public class EscapePlayerMapper {

    @Autowired
    private EscapePlayerRepository escapePlayerRepository;

    public EscapePlayerDto escapePlayerToEscapePlayerDto(EscapePlayer escapePlayer) {
        final EscapePlayerDto escapePlayerDto = new EscapePlayerDto();
        escapePlayerDto.setEscapeId(escapePlayer.getIdEscape());
        escapePlayerDto.setPlayerId(escapePlayer.getIdPlayer());
        escapePlayerDto.setStatus(escapePlayer.getStatus());
        return escapePlayerDto;
    }
}
