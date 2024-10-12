package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pedribault.game.enums.MissionConditionEnum;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.MissionMapper;
import pedribault.game.model.*;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateClue;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateClueWithId;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateMission;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateMissionOption;
import pedribault.game.model.dto.MissionDto;
import pedribault.game.repository.ClueRepository;
import pedribault.game.repository.EscapeRepository;
import pedribault.game.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pedribault.game.repository.PlayerRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private MissionMapper missionMapper;
    @Autowired
    private EscapeRepository escapeRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ClueRepository clueRepository;

    public List<MissionDto> getMissions() {
        final List<Mission> missions = missionRepository.findAll() == null ? new ArrayList<>() : missionRepository.findAll();
        final List<MissionDto> missionDtos = new ArrayList<>();
        for (final Mission mission : missions) {
            missionDtos.add(missionMapper.missionToMissionDto(mission));
        }
        return missionDtos;
    }

    public MissionDto getMissionById(final Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The id can't be null", "The provided id is null.");
        }

        final Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Missions table", "The id " + id + " does not exist."));
        return missionMapper.missionToMissionDto(mission);
    }

    public MissionDto createMission(CreateOrUpdateMission createOrUpdateMission) {
        if (createOrUpdateMission == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Mission is null", "A body is required");
        }
        setDefaultValues(createOrUpdateMission);
        Mission mission = null;

        mission = setMissionType(createOrUpdateMission);
        mission.setTitle(createOrUpdateMission.getTitle());
        mission.setDescription(createOrUpdateMission.getDescription());
        mission.setIsVisible(createOrUpdateMission.getIsVisible());
        mission.setIsOptional(createOrUpdateMission.getIsOptional());
        mission.setOrder(createOrUpdateMission.getOrder());

        if (createOrUpdateMission.getEscapeId() != null) {
            final Escape escape = escapeRepository.findById(createOrUpdateMission.getEscapeId())
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Escape not found", "Escape with id: " + createOrUpdateMission.getEscapeId() + " doesn't exist in the Escapes database"));
            mission.setEscape(escape);
        }

        if (createOrUpdateMission.getClues() != null && !createOrUpdateMission.getClues().isEmpty()) {
            addClues(createOrUpdateMission.getClues(), mission);
        }

        if (createOrUpdateMission.getMissionOptions() != null) {
            setMissionOptions(createOrUpdateMission, mission);
        }

        missionRepository.save(mission);

        return missionMapper.missionToMissionDto(mission);
    }

    private static void setDefaultValues(final CreateOrUpdateMission createOrUpdateMission) {
        if (createOrUpdateMission.getIsVisible() == null) {
            log.info("isVisible is null : set to true");
            createOrUpdateMission.setIsVisible(true);
        }
        if (createOrUpdateMission.getIsOptional() == null) {
            log.info("isOptional is null : set to false");
            createOrUpdateMission.setIsOptional(false);
        }
        if (createOrUpdateMission.getOrder() == null) {
            log.info("order is null : set to 0");
            createOrUpdateMission.setOrder(0);
        }
    }

    private Mission setMissionType(final CreateOrUpdateMission createOrUpdateMission) {
        Mission mission;
        if (Objects.equals(createOrUpdateMission.getMissionType(), "STANDARD")) {
            mission = new StandardMission();
            ((StandardMission) mission).setSuccessRate(createOrUpdateMission.getSuccessRate());
        } else if (Objects.equals(createOrUpdateMission.getMissionType(), "CUSTOM")) {
            mission = new CustomMission();
            ((CustomMission) mission).setSubOrder(createOrUpdateMission.getSubOrder());
        } else {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Mission type unknown", "Mission type must be STANDARD or CUSTOM");
        }
        return mission;
    }

    private void addClues(final List<CreateOrUpdateClueWithId> createOrUpdateClueWithIds, final Mission mission) {
        for (CreateOrUpdateClueWithId createOrUpdateClueWithId : createOrUpdateClueWithIds) {
            final Clue clue = new Clue();
            clue.setMission(mission);
            clue.setOrder(createOrUpdateClueWithId.getOrder());
            clue.setContent(createOrUpdateClueWithId.getContent());
            clue.setSubOrder(createOrUpdateClueWithId.getSubOrder());
            mission.addClue(clue);
        }
    }

    private static void setMissionOptions(final CreateOrUpdateMission createOrUpdateMission, final Mission mission) {
        final List<MissionOption> missionOptions = new ArrayList<>();
        for (CreateOrUpdateMissionOption createOrUpdateMissionOption : createOrUpdateMission.getMissionOptions()) {
            final MissionOption missionOption = new MissionOption();
            missionOption.setMission(mission);
            if (createOrUpdateMissionOption.getConditions() == null || createOrUpdateMissionOption.getConditions().isEmpty()) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, "Missing condition", "The MissionOption: " + createOrUpdateMissionOption.toString() + "is missing a condition");
            }
            final List<MissionConditionEnum> missionConditionEnums = new ArrayList<>();
            for (String condition : createOrUpdateMissionOption.getConditions()) {
                MissionConditionEnum missionConditionEnum;
                try {
                    missionConditionEnum = MissionConditionEnum.valueOf(condition);
                } catch (IllegalArgumentException e) {
                    throw new TheGameException(HttpStatus.BAD_REQUEST, "One condition dosen't exist", "The condition: " + condition + " doens't exist");
                }
                missionConditionEnums.add(missionConditionEnum);
            }
            missionOption.setConditions(missionConditionEnums);
            missionOption.setDescription(createOrUpdateMissionOption.getDescription());
            if (createOrUpdateMissionOption.getClues() != null && !createOrUpdateMissionOption.getClues().isEmpty()) {
                final List<Clue> clues = new ArrayList<>();
                for (CreateOrUpdateClue createOrUpdateClue : createOrUpdateMissionOption.getClues()) {
                    final Clue clue = new Clue();
                    clue.setMissionOption(missionOption);
                    clue.setContent(createOrUpdateClue.getContent());
                    clue.setOrder(createOrUpdateClue.getOrder());
                    clue.setSubOrder(createOrUpdateClue.getSubOrder());
                    clues.add(clue);
                }
                missionOption.setClues(clues);
            }
        }
        mission.setOptions(missionOptions);
    }

    public MissionDto updateMission(Integer id, CreateOrUpdateMission createOrUpdateMission) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id not provided", "The id must be provided");
        } else if (createOrUpdateMission == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The input mission is null", "The body is missing");
        }

        log.info("RETREIVING MISSION ID = " + id);

        Mission existingMission = missionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission not found", "The id " + id + "doesn't exist in the Missions database"));
        log.info("MISSION RETRIEVED");

        updateCommonValues(createOrUpdateMission, existingMission);

        if (createOrUpdateMission.getEscapeId() != null) {
            final Escape escape = escapeRepository.findById(createOrUpdateMission.getEscapeId())
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Escape not found", "Escape with id: " + createOrUpdateMission.getEscapeId() + "doesn't exist in Escape database"));
            existingMission.setEscape(escape);
        }

        if (createOrUpdateMission.getClues() != null) {
            handleClues(createOrUpdateMission, existingMission);
        }

        if (existingMission instanceof CustomMission) {
            if (!Objects.equals(createOrUpdateMission.getMissionType(), "CUSTOM")) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, "Wrong mission type", "Mission type given is: " + createOrUpdateMission.getMissionType() + " and found Mission is CUSTOM");
            }
            ((CustomMission) existingMission).setSubOrder(createOrUpdateMission.getSubOrder());
        } else if (existingMission instanceof StandardMission) {
            if (!Objects.equals(createOrUpdateMission.getMissionType(), "STANDARD")) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, "Wrong mission type", "Mission type given is: " + createOrUpdateMission.getMissionType() + " and found Mission is STANDARD");
            }
            if (createOrUpdateMission.getSuccessRate() != null) {
                ((StandardMission) existingMission).setSuccessRate(createOrUpdateMission.getSuccessRate());
            }
        }

        // TODO handle players via MissionPlayerMapping

        missionRepository.save(existingMission);
        final MissionDto missionDto = missionMapper.missionToMissionDto(existingMission);
        return missionDto;
    }

    private static void updateCommonValues(final CreateOrUpdateMission createOrUpdateMission, final Mission existingMission) {
        if (createOrUpdateMission.getTitle() != null) {
            existingMission.setTitle(createOrUpdateMission.getTitle());
        }
        if (createOrUpdateMission.getDescription() != null) {
            existingMission.setDescription(createOrUpdateMission.getDescription());
        }
        if (createOrUpdateMission.getIsVisible() != null) {
            existingMission.setIsVisible(createOrUpdateMission.getIsVisible());
        }
        if (createOrUpdateMission.getIsOptional() != null) {
            existingMission.setIsOptional(createOrUpdateMission.getIsOptional());
        }
        if (createOrUpdateMission.getOrder() != null) {
            existingMission.setOrder(createOrUpdateMission.getOrder());
        }
    }

    private void handleClues(final CreateOrUpdateMission createOrUpdateMission, final Mission existingMission) {
        // add new Clues (without id)
        final List<CreateOrUpdateClueWithId> newClues =
                createOrUpdateMission.getClues().stream().filter(c -> c.getId() == null).toList();
        addClues(newClues, existingMission);
        final List<CreateOrUpdateClueWithId> givenClues =
                createOrUpdateMission.getClues().stream().filter(c -> c.getId() != null).toList();
        final Set<Integer> distinctClueIds = givenClues.stream().map(CreateOrUpdateClueWithId::getId).collect(Collectors.toSet());
        final List<Clue> foundClues = clueRepository.findAllById(distinctClueIds);
        if (foundClues.size() != distinctClueIds.size()) {
            final List<Integer> foundClueIds = foundClues.stream().map(Clue::getId).toList();
            final List<Integer> missingClueIds = distinctClueIds.stream().filter(cid -> !foundClueIds.contains(cid)).toList();
            throw new TheGameException(HttpStatus.NOT_FOUND,
                    "Clues not found",
                    "The following ids were not found: " + missingClueIds + " in the Clues database");
        }
        // add clues
        for (Clue foundClue : foundClues) {
            if (!existingMission.getClues().contains(foundClue)) {
                existingMission.addClue(foundClue);
            }
        }
        // remove clues
        for (Clue clue : existingMission.getClues()) {
            if (!foundClues.contains(clue)) {
                existingMission.removeClue(clue);
            }
        }
    }

    // TODO
    // addClue, removeClue
}