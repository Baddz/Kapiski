package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pedribault.game.dto.EscapeDto;
import pedribault.game.dto.PlayerStatus;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.EscapeMapper;
import pedribault.game.model.*;
import pedribault.game.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EscapeService {

    @Autowired
    private EscapeRepository escapeRepository;
    @Autowired
    private UniverseRepository universeRepository;
    @Autowired
    private EscapeMapper escapeMapper;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private EscapePlayerRepository escapePlayerRepository;
    @Autowired
    private MissionRepository missionRepository;

    public List<EscapeDto> getEscapes() {
        final List<Escape> escapes =escapeRepository.findAll() == null ? new ArrayList<>() : escapeRepository.findAll();
        final List<EscapeDto> escapeDtos = new ArrayList<>();
        if (!escapes.isEmpty()) {
            escapeDtos.addAll(escapes.stream().map(e -> escapeMapper.escapeToEscapeDto(e)).toList());
        }
        return escapeDtos;
    }

    public EscapeDto getEscapeById(final Integer id) {
        EscapeDto escapeDto;
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The id can't be null", "The provided id is null.");
        } else {
            Escape escape = escapeRepository.findById(id)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Escapes table", "The id " + id + " does not exist."));
            escapeDto = escapeMapper.escapeToEscapeDto(escape);
        }

        return escapeDto;
    }

    public EscapeDto createEscape(final EscapeDto escapeDto) {

        if (escapeDto == null || escapeDto.getTitle() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Title is null", "Title is required");
        }

        final Escape escape = new Escape();
        final Escape newEscape = escapeMapper.escapeDtoToEscape(escapeDto);
        escape.setEscapePlayers(newEscape.getEscapePlayers());
        escape.setTitle(newEscape.getTitle());
        escape.setDifficulty(newEscape.getDifficulty());
        escape.setMissions(newEscape.getMissions());
        escape.setUniverse(newEscape.getUniverse());
        escape.setSuccessRate(newEscape.getSuccessRate());
        escapeRepository.save(escape);

        escapeDto.setId(escape.getId());
        return escapeDto;
    }

    public EscapeDto updateEscape(EscapeDto escapeDto) {
        if (escapeDto == null || escapeDto.getId() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id not provided", "The id must be provided in the body");
        }

        log.info("RETREIVING ESCAPE ID = " + escapeDto.getId());
        Escape existingEscape = escapeRepository.findById(escapeDto.getId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Escape not found", "The id " + escapeDto.getId() + "doesn't exist in the Escapes database"));
        log.info("ESCAPE RETREIVED");

        final AtomicBoolean updated = new AtomicBoolean(false);

        if (escapeDto.getTitle() != null && escapeDto.getTitle() != existingEscape.getTitle()) {
            existingEscape.setTitle(escapeDto.getTitle());
            updated.set(true);
        }

        updateEscapePlayers(escapeDto, existingEscape, updated);

        updateMissions(escapeDto, existingEscape, updated);

        Integer updatedDifficulty = escapeDto.getDifficulty();
        if (updatedDifficulty != null && !Objects.equals(existingEscape.getDifficulty(), updatedDifficulty)) {
            existingEscape.setDifficulty(updatedDifficulty);
            updated.set(true);
        }

        Double updatedSuccessRate = escapeDto.getSuccessRate();
        if (updatedSuccessRate != null && !Objects.equals(existingEscape.getSuccessRate(), updatedSuccessRate)) {
            existingEscape.setSuccessRate(updatedSuccessRate);
            updated.set(true);
        }

        Integer universeId = escapeDto.getUniverseId();
        if (universeId != null && existingEscape.getUniverse() != null && existingEscape.getUniverse().getId() != escapeDto.getUniverseId()) {
            final Universe newUniverse = universeRepository.findById(universeId).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Invalid Universe id", "In the body, please set the Universe's id to null, remove the Universe, or give a valid Universe id"));
            existingEscape.setUniverse(newUniverse);
            updated.set(true);
        }

        if (updated.get()) {
            escapeRepository.save(existingEscape);
        }

        return escapeMapper.escapeToEscapeDto(existingEscape);
    }

    private void updateMissions(final EscapeDto escapeDto, final Escape existingEscape, final AtomicBoolean updated) {
        final List<Integer> missionIds = escapeDto.getMissionIds();
        if (missionIds != null) {
            Set<Integer> existingMissionIds = existingEscape.getMissions()
                    .stream()
                    .map(Mission::getId)
                    .collect(Collectors.toSet());
            List<Mission> missions = missionRepository.findAllById(missionIds);
            if (missions.size() != existingMissionIds.size()) {
                List<Integer> foundMissionIds = missions.stream().map(Mission::getId).toList();
                List<Integer> missingMissionIds = missionIds.stream()
                        .filter(id -> !foundMissionIds.contains(id))
                        .toList();
                throw new TheGameException(HttpStatus.NOT_FOUND,
                        "Some missions don't exist in the Missions database",
                        "The following mission ids were not found: " + missingMissionIds);
            }
            missions.forEach(mission -> {
                if (!existingMissionIds.contains(mission.getId())) {
                    existingEscape.addMission(mission);
                    updated.set(true);
                }
            });
            existingEscape.getMissions().forEach(m -> {
                if (!existingMissionIds.contains(m.getId())) {
                    existingEscape.removeMission(m);
                }
            });
        }
    }

    private void updateEscapePlayers(final EscapeDto escapeDto, final Escape existingEscape, final AtomicBoolean updated) {
        final List<PlayerStatus> playerStatuses = escapeDto.getPlayers();
        if (playerStatuses != null) {
            Set<Integer> existingPlayerIds = existingEscape.getEscapePlayers()
                    .stream()
                    .map(ep -> ep.getPlayer().getId())
                    .collect(Collectors.toSet());

            playerStatuses.forEach(epdto -> {
                if (existingPlayerIds.contains(epdto.getPlayerId())) {
                    // The player is already linked to the escape; we skip
                    return;
                }
                Optional<EscapePlayer> escapePlayerOptional = escapePlayerRepository.findByEscapeIdAndPlayerId(existingEscape.getId(), epdto.getPlayerId());
                if (escapePlayerOptional.isPresent()) {
                    // The EscapePlayer exists, we add it
                    existingEscape.addEscapePlayer(escapePlayerOptional.get());
                    updated.set(true);
                } else {
                    // The EscapePlayer doesn't exist, we create then add it
                    final Player player = playerRepository.findById(epdto.getPlayerId()).orElseThrow(() ->
                            new TheGameException(HttpStatus.NOT_FOUND, "One of the players submitted doesn't exist in the Players database", "The player with id " + epdto.getPlayerId() + " doesn't exist."));
                    final EscapePlayer escapePlayer = new EscapePlayer(player, existingEscape, epdto.getStatus());
                    existingEscape.addEscapePlayer(escapePlayer);
                    updated.set(true);
                }
            });
        }
    }
}

