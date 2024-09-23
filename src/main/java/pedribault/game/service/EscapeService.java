package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pedribault.game.dto.*;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.EscapeMapper;
import pedribault.game.model.*;
import pedribault.game.model.StandardMission;
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

    public EscapeDto createEscape(EscapeDto escapeDto) {

        if (escapeDto == null || escapeDto.getTitle() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Title is null", "Title is required");
        }

        final Escape escape = new Escape();
        final Escape newEscape = escapeMapper.escapeDtoToEscape(escapeDto);
        escape.setEscapePlayers(newEscape.getEscapePlayers());
        escape.setTitle(newEscape.getTitle());
        escape.setDifficulty(newEscape.getDifficulty());
        escape.setStandardMissions(newEscape.getStandardMissions());
        escape.setUniverse(newEscape.getUniverse());
        escape.setSuccessRate(newEscape.getSuccessRate());
        escapeRepository.save(escape);

        escapeDto = escapeMapper.escapeToEscapeDto(escape);
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

        UniverseSummary universe = escapeDto.getUniverse();
        if (universe != null && existingEscape.getUniverse() != null && !Objects.equals(existingEscape.getUniverse().getId(), escapeDto.getUniverse().getId())) {
            final Universe newUniverse = universeRepository.findById(universe.getId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Invalid Universe id", "In the body, please set the Universe's id to null, remove the Universe, or give a valid Universe id"));
            existingEscape.setUniverse(newUniverse);
            updated.set(true);
        }

        if (updated.get()) {
            escapeRepository.save(existingEscape);
        }

        return escapeMapper.escapeToEscapeDto(existingEscape);
    }

    private void updateMissions(final EscapeDto escapeDto, final Escape existingEscape, final AtomicBoolean updated) {
        if (escapeDto.getMissions() != null) {
            final List<Integer> missionIds = escapeDto.getMissions().stream().map(m -> m.getStandardMissionSummary().getId()).toList();

            Set<Integer> existingMissionIds = existingEscape.getStandardMissions()
                    .stream()
                    .map(StandardMission::getId)
                    .collect(Collectors.toSet());
            List<Mission> standardMissions = missionRepository.findAllById(missionIds);
            if (standardMissions.size() != existingMissionIds.size()) {
                List<Integer> foundMissionIds = standardMissions.stream().map(Mission::getId).toList();
                List<Integer> missingMissionIds = missionIds.stream()
                        .filter(id -> !foundMissionIds.contains(id))
                        .toList();
                throw new TheGameException(HttpStatus.NOT_FOUND,
                        "Some missions don't exist in the Missions database",
                        "The following mission ids were not found: " + missingMissionIds);
            }
            standardMissions.forEach(mission -> {
                if (!existingMissionIds.contains(mission.getId())) {
                    existingEscape.addMission((StandardMission) mission);
                    updated.set(true);
                }
            });
            existingEscape.getStandardMissions().forEach(m -> {
                if (!existingMissionIds.contains(m.getId())) {
                    existingEscape.removeMission(m);
                }
            });
        }
    }

    private void updateEscapePlayers(final EscapeDto escapeDto, final Escape existingEscape, final AtomicBoolean updated) {
        final List<PlayerSummaryEscape> playerSummaryEscapes = escapeDto.getPlayers();
        if (playerSummaryEscapes != null) {
            Set<Integer> existingPlayerIds = existingEscape.getEscapePlayers()
                    .stream()
                    .map(ep -> ep.getPlayer().getId())
                    .collect(Collectors.toSet());

            playerSummaryEscapes.forEach(epdto -> {
                if (existingPlayerIds.contains(epdto.getPlayerSummary().getId())) {
                    // The player is already linked to the escape; we skip
                    return;
                }
                Optional<EscapePlayer> escapePlayerOptional = escapePlayerRepository.findByEscapeIdAndPlayerId(existingEscape.getId(), epdto.getPlayerSummary().getId());
                if (escapePlayerOptional.isPresent()) {
                    // The EscapePlayer exists, we add it
                    existingEscape.addEscapePlayer(escapePlayerOptional.get());
                    updated.set(true);
                } else {
                    // The EscapePlayer doesn't exist, we create then add it
                    final Player player = playerRepository.findById(epdto.getPlayerSummary().getId()).orElseThrow(() ->
                            new TheGameException(HttpStatus.NOT_FOUND, "One of the players submitted doesn't exist in the Players database", "The player with id " + epdto.getPlayerSummary().getId() + " doesn't exist."));
                    final EscapePlayer escapePlayer = new EscapePlayer(player, existingEscape, epdto.getStatus());
                    existingEscape.addEscapePlayer(escapePlayer);
                    updated.set(true);
                }
            });
        }
    }
}

