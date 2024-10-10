package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pedribault.game.enums.MissionConditionEnum;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.MissionMapper;
import pedribault.game.model.*;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateClue;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateMission;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateMissionOption;
import pedribault.game.model.dto.MissionDto;
import pedribault.game.repository.ClueRepository;
import pedribault.game.repository.EscapeRepository;
import pedribault.game.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pedribault.game.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            setClues(createOrUpdateMission, mission);
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
        if (createOrUpdateMission.getMissionType() == "STANDARD") {
            mission = new StandardMission();
            ((StandardMission) mission).setSuccessRate(createOrUpdateMission.getSuccessRate());
        } else if (createOrUpdateMission.getMissionType() == "CUSTOM") {
            mission = new CustomMission();
            ((CustomMission) mission).setSubOrder(createOrUpdateMission.getSubOrder());
            if (createOrUpdateMission.getPlayerIds() != null) {
                final Set<Integer> distinctPlayerIds = new HashSet<>(createOrUpdateMission.getPlayerIds());
                final List<Player> foundPlayers = playerRepository.findAllById(distinctPlayerIds);
                if (foundPlayers.size() != distinctPlayerIds.size()) {
                    final List<Integer> foundIds = foundPlayers.stream().map(Player::getId).toList();
                    final List<Integer> missingIds = distinctPlayerIds.stream().filter(id -> !foundIds.contains(id)).toList();
                    throw new TheGameException(HttpStatus.NOT_FOUND,
                            "Players not found",
                            "The following ids were not found: " + missingIds + " in the PLayers database");
                }
                ((CustomMission) mission).setPlayers(foundPlayers);
            }
        } else {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Mission type unknown", "Mission type must be STANDARD or CUSTOM");
        }
        return mission;
    }

    private void setClues(final CreateOrUpdateMission createOrUpdateMission, final Mission mission) {
        final List<Clue> clues = new ArrayList<>();
        for (CreateOrUpdateClue createOrUpdateClue : createOrUpdateMission.getClues()) {
            final Clue clue = new Clue();
            clue.setMission(mission);
            clue.setOrder(createOrUpdateClue.getOrder());
            clue.setContent(createOrUpdateClue.getContent());
            clue.setSubOrder(createOrUpdateClue.getSubOrder());
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

    public Mission updateMission(Integer id, StandardMission updatedStandardMission) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id not provided", "The id must be provided");
        }
        if (updatedStandardMission == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The input mission is null", "The body is missing");
        }

        Mission existingStandardMission = missionRepository.findById(id).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Universe not found", "The id " + id + "doesn't exist in the Escapes database"));

        if (updatedStandardMission.getTitle() != null) {
            existingStandardMission.setTitle(updatedStandardMission.getTitle());
        }
        if (updatedStandardMission.getIsVisible() != null) {
            existingStandardMission.setIsVisible(updatedStandardMission.getIsVisible());
        }
        if (updatedStandardMission.getIsOptional() != null) {
            existingStandardMission.setIsOptional(updatedStandardMission.getIsOptional());
        }
        if (updatedStandardMission.getOrder() != null) {
            existingStandardMission.setOrder(updatedStandardMission.getOrder());
        }
//        if (updatedStandardMission.getSuccessRate() != null) {
//            existingStandardMission.setSuccessRate(updatedStandardMission.getSuccessRate());
//        }
//        if (updatedStandardMission.getEscape() != null) {
//            Integer updatedEscapeId = updatedStandardMission.getEscape().getId();
//            if (updatedEscapeId != null) {
//                Escape updatedMissionEscape = escapeRepository.findById(updatedEscapeId)
//                        .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission not found", "Mission with id " + updatedEscapeId + " was not found"));
//                existingStandardMission.setEscape(updatedMissionEscape);
//            }
//        }

        return missionRepository.save(existingStandardMission);
    }

}
