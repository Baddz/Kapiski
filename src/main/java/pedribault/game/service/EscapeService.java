package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateEscape;
import pedribault.game.model.dto.EscapeDto;
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
    private MissionRepository missionRepository;

    public List<EscapeDto> getEscapes() {
        final List<Escape> escapes = escapeRepository.findAll() == null ? new ArrayList<>() : escapeRepository.findAll();
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

    public EscapeDto createEscape(CreateOrUpdateEscape createOrUpdateEscape) {

        if (createOrUpdateEscape == null || createOrUpdateEscape.getTitle() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Title is null", "Title is required");
        }
        final Escape escape = new Escape();

        escape.setTitle(createOrUpdateEscape.getTitle());
        escape.setSuccessRate(createOrUpdateEscape.getSuccessRate());
        escape.setDifficulty(createOrUpdateEscape.getDifficulty());

        final List<Integer> playerIds = createOrUpdateEscape.getPlayerIds();
        List<Player> players = new ArrayList<>();
        if (playerIds != null && !playerIds.isEmpty()) {
            final Set<Integer> distinctPlayerIds = new HashSet<>(playerIds);
            players = playerRepository.findAllById(distinctPlayerIds);
            if(players.size() != distinctPlayerIds.size()) {
                final List<Integer> foundIds = players.stream().map(Player::getId).toList();
                final List<Integer> missingIds = distinctPlayerIds.stream().filter(id -> !foundIds.contains(id)).toList();
                throw new TheGameException(HttpStatus.NOT_FOUND,"Players not found", "The following ids were not found: " + missingIds + " in the PLayers database");
            }
        }
        escape.setPlayers(players);

        final Integer universeId = createOrUpdateEscape.getUniverseId();
        if (universeId != null) {
            final Universe universe = universeRepository.findById(universeId).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Universe not found", "The id " + universeId + " doesn't exist in the Universe database"));
            escape.setUniverse(universe);
        }

        final List<Integer> missionIds = createOrUpdateEscape.getMissionIds();
        List<StandardMission> standardMissions = new ArrayList<>();
        if (missionIds != null && !missionIds.isEmpty()) {
            final Set<Integer> distinctMissionIds = new HashSet<>(missionIds);
            final List<Mission> missions = missionRepository.findAllById(distinctMissionIds);
            if (missions.size() != distinctMissionIds.size()) {
                final List<Integer> foundIds = missions.stream().map(Mission::getId).toList();
                final List<Integer> missingIds = distinctMissionIds.stream().filter(id -> !foundIds.contains(id)).toList();
                throw new TheGameException(HttpStatus.NOT_FOUND,"Missions not found", "The following ids were not found: " + missingIds + " in the Missions database");
            }
            missions.forEach(m -> {
                if (!m.getClass().equals(StandardMission.class)) {
                    throw new TheGameException(HttpStatus.NOT_ACCEPTABLE, "Mission is not standard", "Mission with id " + m.getId() + " is not a standard mission");
                }
            });
            standardMissions = missions.stream().map(m -> (StandardMission) m).toList();
        }
        escape.setStandardMissions(standardMissions);

        escapeRepository.save(escape);

        return escapeMapper.escapeToEscapeDto(escape);
    }

    public EscapeDto updateEscape(CreateOrUpdateEscape createOrUpdateEscape, Integer id) {
        if (createOrUpdateEscape == null || id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id or body not provided", "The id and the body must be provided");
        }

        log.info("RETREIVING ESCAPE ID = " + id);
        Escape existingEscape = escapeRepository.findById(id).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Escape not found", "The id " + id + "doesn't exist in the Escapes database"));
        log.info("ESCAPE RETREIVED");

        final AtomicBoolean updated = new AtomicBoolean(false);

        if (createOrUpdateEscape.getTitle() != null && !createOrUpdateEscape.getTitle().equals(existingEscape.getTitle())) {
            existingEscape.setTitle(createOrUpdateEscape.getTitle());
            updated.set(true);
        }

        updatePlayers(createOrUpdateEscape, existingEscape, updated);

        updateMissions(createOrUpdateEscape, existingEscape, updated);

        Integer updatedDifficulty = createOrUpdateEscape.getDifficulty();
        if (updatedDifficulty != null && !Objects.equals(existingEscape.getDifficulty(), updatedDifficulty)) {
            existingEscape.setDifficulty(updatedDifficulty);
            updated.set(true);
        }

        Double updatedSuccessRate = createOrUpdateEscape.getSuccessRate();
        if (updatedSuccessRate != null && !Objects.equals(existingEscape.getSuccessRate(), updatedSuccessRate)) {
            existingEscape.setSuccessRate(updatedSuccessRate);
            updated.set(true);
        }

        Integer universeId = createOrUpdateEscape.getUniverseId();
        if (universeId != null) {
            if (existingEscape.getUniverse() == null || !Objects.equals(existingEscape.getUniverse().getId(), createOrUpdateEscape.getUniverseId())) {
                final Universe newUniverse = universeRepository.findById(universeId).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Universe not found", "The universe id " + universeId + " doesn't exist in the Universes database"));
                existingEscape.setUniverse(newUniverse);
                updated.set(true);
            }
        }

        if (updated.get()) {
            escapeRepository.save(existingEscape);
        }

        return escapeMapper.escapeToEscapeDto(existingEscape);
    }

    private void updateMissions(final CreateOrUpdateEscape createOrUpdateEscape, final Escape existingEscape, final AtomicBoolean updated) {
        if (createOrUpdateEscape.getMissionIds() != null) {
            final List<Integer> missionIds = createOrUpdateEscape.getMissionIds();

            Set<Integer> existingMissionIds = existingEscape.getStandardMissions()
                    .stream()
                    .map(StandardMission::getId)
                    .collect(Collectors.toSet());
            List<Mission> foundMissions = missionRepository.findAllById(missionIds);
            List<Integer> foundMissionIds = foundMissions.stream().map(Mission::getId).toList();
            List<Integer> missingMissionIds = missionIds.stream()
                    .filter(id -> (!foundMissionIds.contains(id)))
                    .toList();

            if (!missingMissionIds.isEmpty()) {
                throw new TheGameException(HttpStatus.NOT_FOUND,
                        "Standard missions not found",
                        "The following standard mission ids were not found in the Missions database: " + missingMissionIds);
            }

            final List<Integer> nonStandardMissions = new ArrayList<>();
            foundMissions.forEach(m -> {
                if (!m.getClass().equals(StandardMission.class)) {
                    nonStandardMissions.add(m.getId());
                }
            });
            if (!nonStandardMissions.isEmpty()) {
                throw new TheGameException(HttpStatus.NOT_FOUND,
                        "Some missions provided are not standard missions",
                        "The following missions are not standard and can not be added to an escape : " + nonStandardMissions);
            }

            foundMissions.forEach(mission -> {
                if (!existingMissionIds.contains(mission.getId())) {
                    existingEscape.addMission((StandardMission) mission);
                    updated.set(true);
                }
            });
            existingEscape.getStandardMissions().forEach(m -> {
                if (!foundMissions.contains(m)) {
                    existingEscape.removeMission(m);
                    updated.set(true);
                }
            });
        }
    }

    private void updatePlayers(final CreateOrUpdateEscape createOrUpdateEscape, final Escape existingEscape, final AtomicBoolean updated) {
        final List<Integer> playerIds = createOrUpdateEscape.getPlayerIds();
        if (playerIds != null) {
            Set<Integer> existingPlayerIds = existingEscape.getPlayers()
                    .stream()
                    .map(Player::getId)
                    .collect(Collectors.toSet());
            List<Player> foundPlayers = playerRepository.findAllById(playerIds);
            List<Integer> foundPlayerIds = foundPlayers.stream().map(Player::getId).toList();
            List<Integer> missingPlayerIds = playerIds.stream().filter(id -> !foundPlayerIds.contains(id)).toList();
            if (!missingPlayerIds.isEmpty()) {
                throw new TheGameException(HttpStatus.NOT_FOUND, "Players not found", "The following standard mission ids were not found in the Players database: " + missingPlayerIds);
            }

            foundPlayers.forEach(p -> {
                if (!existingPlayerIds.contains(p.getId())) {
                    existingEscape.addPlayer(p);
                    updated.set(true);
                }
            });
            existingEscape.getPlayers().forEach(p -> {
                if (!foundPlayers.contains(p)) {
                    existingEscape.removePlayer(p);
                    updated.set(true);
                }
            });
        }
    }
}

