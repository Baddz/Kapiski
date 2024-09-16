package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.dto.EscapeDto;
import pedribault.game.dto.EscapePlayerDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Escape;
import pedribault.game.model.EscapePlayer;
import pedribault.game.model.Mission;
import pedribault.game.model.Universe;
import pedribault.game.repository.EscapePlayerRepository;
import pedribault.game.repository.EscapeRepository;
import pedribault.game.repository.MissionRepository;
import pedribault.game.repository.UniverseRepository;

import java.util.List;

@Component
public class EscapeMapper {

    @Autowired
    private UniverseRepository universeRepository;
    private EscapePlayerMapper escapePlayerMapper;
    @Autowired
    private EscapePlayerRepository escapePlayerRepository;
    @Autowired
    private MissionRepository missionRepository;

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

    public Escape escapeDtoToEscape(EscapeDto escapeDto) {
        final Escape escape = new Escape();
        if (escapeDto.getId() != null) {
            escape.setId(escapeDto.getId());
        }
        escape.setTitle(escapeDto.getTitle());
        escape.setDifficulty(escapeDto.getDifficulty());
        escape.setSuccessRate(escapeDto.getSuccessRate());
        if (escapeDto.getUniverseId() != null) {
            Universe universe = universeRepository.findById(escapeDto.getUniverseId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Universes table", "The id " + escapeDto.getUniverseId() + " does not exist."));
        }
        if (escapeDto.getEscapePlayerDtos() != null) {
            escapeDto.getEscapePlayerDtos().forEach(epdto -> {
                EscapePlayer escapePlayer = escapePlayerRepository.findByEscapeIdAndPlayerId(epdto.getEscapeId(), epdto.getPlayerId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "The EscapePlayer with escapeId " + epdto.getEscapeId() + " and playerId " + epdto.getPlayerId() + " doesn't exist", "The EscapePlayer does not exist in the Escape_J_Players databas."));
                escape.addEscapePlayer(escapePlayer);
            });
        }
        if (escapeDto.getMissionIds() != null) {
            escapeDto.getMissionIds().forEach(mid -> {
                Mission mission = missionRepository.findById(mid).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Missions table", "The id " + mid + " does not exist."));
                escape.addMission(mission);
            });
        }
        return escape;
    }
}
