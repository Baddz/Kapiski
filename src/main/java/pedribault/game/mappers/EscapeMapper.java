package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.dto.EscapeDto;
import pedribault.game.dto.PlayerSummaryEscape;
import pedribault.game.enums.EscapeStatusEnum;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.*;
import pedribault.game.repository.EscapePlayerRepository;
import pedribault.game.repository.MissionRepository;
import pedribault.game.repository.PlayerRepository;
import pedribault.game.repository.UniverseRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component
public class EscapeMapper {

    @Autowired
    private UniverseRepository universeRepository;
    @Autowired
    private EscapePlayerRepository escapePlayerRepository;
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private UniverseMapper universeMapper;
    @Autowired
    private ToSummaryMapper toSummaryMapper;
    @Autowired
    private MissionMapper missionMapper;
    @Autowired
    private PlayerMapper playerMapper;


    public EscapeDto escapeToEscapeDto(Escape escape) {
        final EscapeDto escapeDto = new EscapeDto();
        escapeDto.setId(escape.getId());
        escapeDto.setTitle(escape.getTitle());
        escapeDto.setDifficulty(escape.getDifficulty());
        escapeDto.setSuccessRate(escape.getSuccessRate());
        if (escape.getUniverse() != null) {
            escapeDto.setUniverse(toSummaryMapper.universeToUniverseSummary(escape.getUniverse()));
        }
        if (escape.getStandardMissions() != null) {
            if (escape.getStandardMissions().isEmpty()) {
                escapeDto.setMissions(new ArrayList<>());
            } else {
                escapeDto.setMissions(toSummaryMapper.standardMissionsToMissionSummaryEscapes(escape.getStandardMissions()));
            }
        }
        if (escape.getEscapePlayers() != null) {
            if (escape.getEscapePlayers().isEmpty()) {
                escapeDto.setPlayers(new ArrayList<>());
            } else {
                final List<PlayerSummaryEscape> playerSummaryEscapes = escape.getEscapePlayers().stream().map(ep -> toSummaryMapper.playerToPlayerSummaryEscape(ep.getPlayer())).toList();
                escapeDto.setPlayers(playerSummaryEscapes);
            }
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
        if (escapeDto.getUniverse() != null) {
            Universe universe = universeRepository.findById(escapeDto.getUniverse().getId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Universes table", "The id " + escapeDto.getUniverse().getId() + " does not exist."));
            escape.setUniverse(universe);
        }
        if (escapeDto.getPlayers() != null) {
            if (escapeDto.getPlayers().isEmpty()) {
                escape.setEscapePlayers(new ArrayList<>());
            } else {
                List<Player> players = playerRepository.findAllById(escapeDto.getPlayers().stream().map(pse -> pse.getPlayerSummary().getId()).toList());
                HashSet<PlayerSummaryEscape> playersSet = new HashSet<>(escapeDto.getPlayers());
                if (players.size() != playersSet.size()) {
                    List<Integer> missingPlayerIds = players.stream().filter(p -> !playersSet.contains(p)).map(Player::getId).toList();
                    throw new TheGameException(HttpStatus.NOT_FOUND, "Some players don't exist in the Players database", "Players with id " + missingPlayerIds + " don't exist");
                }
                escapeDto.getPlayers().forEach(ps -> {
                    final Player player = players.stream().filter(p -> Objects.equals(p.getId(), ps.getPlayerSummary().getId())).toList().get(0);
                    final EscapePlayer escapePlayer = new EscapePlayer(player, escape, EscapeStatusEnum.NEW);
                    escape.addEscapePlayer(escapePlayer);
                });
            }
        }
        if (escapeDto.getMissions() != null) {
            escapeDto.getMissions().forEach(m -> {
                StandardMission standardMission = (StandardMission) missionRepository.findById(m.getStandardMissionSummary().getId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Missions table", "The id " + m.getStandardMissionSummary().getId() + " does not exist."));
                escape.addMission(standardMission);
            });
        }
        return escape;
    }
}
