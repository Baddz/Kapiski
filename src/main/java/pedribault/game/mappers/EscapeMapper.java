package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pedribault.game.dto.EscapeDto;
import pedribault.game.dto.EscapePlayerDto;
import pedribault.game.model.Escape;
import pedribault.game.repository.EscapeRepository;

import java.util.List;

@Component
public class EscapeMapper {

    @Autowired
    private EscapeRepository escapeRepository;
    private EscapePlayerMapper escapePlayerMapper;

    public EscapeDto escapeToEscapeDto(Escape escape) {
        final EscapeDto escapeDto = new EscapeDto();
        escapeDto.setId(escape.getId());
        escapeDto.setTitle(escape.getTitle());
        escapeDto.setDifficulty(escape.getDifficulty());
        escapeDto.setSuccessRate(escape.getSuccessRate());
        if (escape.getUniverse() != null) {
            escapeDto.setUniverseId(escape.getUniverse().getId());
        }
        if (escape.getMissions() != null) {
            final List<Integer> missionIds = escape.getMissions().stream().map(m -> m.getId()).toList();
            escapeDto.setMissionIds(missionIds);
        }
        if (escape.getEscapePlayers() != null) {
            final List<EscapePlayerDto> escapePlayerDtos = escape.getEscapePlayers().stream().map(ep -> escapePlayerMapper.escapePlayerToEscapePlayerDto(ep)).toList();
            escapeDto.setEscapePlayerDtos(escapePlayerDtos);
        }
        return escapeDto;
    }
}
