package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.dto.EscapeDto;
import pedribault.game.dto.PlayerStatus;
import pedribault.game.enums.EscapeStatusEnum;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.*;
import pedribault.game.repository.EscapePlayerRepository;
import pedribault.game.repository.MissionRepository;
import pedribault.game.repository.PlayerRepository;
import pedribault.game.repository.UniverseRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EscapeMapper {

    @Autowired
    private UniverseRepository universeRepository;
    @Autowired
    private EscapePlayerMapper escapePlayerMapper;
    @Autowired
    private EscapePlayerRepository escapePlayerRepository;
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private PlayerRepository playerRepository;

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
            final List<Integer> missionIds = escape.getMissions().stream().map(Mission::getId).toList();
            escapeDto.setMissionIds(missionIds);
        }
        if (escape.getEscapePlayers() != null) {
            final List<PlayerStatus> playerStatuses = escape.getEscapePlayers().stream().map(ep -> escapePlayerMapper.escapePlayerToEscapePlayerDto(ep)).toList();
            escapeDto.setPlayers(playerStatuses);
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
        if (escapeDto.getPlayers() != null) {
            List<Player> players = playerRepository.findAllById(escapeDto.getPlayers().stream().map(PlayerStatus::getPlayerId).toList());
            HashSet<PlayerStatus> playersSet = new HashSet<>(escapeDto.getPlayers());
            if (players.size() != playersSet.size()) {
                List<Integer> missingPlayerIds = players.stream().map(Player::getId).filter(p -> !playersSet.contains(p)).toList();
                throw new TheGameException(HttpStatus.NOT_FOUND, "Some players don't exist in the Players database","Players with id " + missingPlayerIds + " don't exist");
            }
            escapeDto.getPlayers().forEach(ps -> {
                final Player player = players.stream().filter(p -> p.getId() == ps.getPlayerId()).toList().get(0);
                final EscapePlayer escapePlayer = new EscapePlayer(player, escape, EscapeStatusEnum.NEW);
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
