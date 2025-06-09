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
        final List<Mission> missions = missionRepository.findAll();
        final List<MissionDto> missionDtos = new ArrayList<>();
        for (final Mission mission : missions) {
            missionDtos.add(missionMapper.missionToMissionDto(mission));
        }
        return missionDtos;
    }

    public MissionDto getMissionById(final Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[getMissionById] Id is null", 
                "[getMissionById] mission_id must be provided");
        }

        final Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[getMissionById] Mission not found", 
                    "[getMissionById] mission_id: " + id + " doesn't exist"));
        return missionMapper.missionToMissionDto(mission);
    }

    public MissionDto createMission(CreateOrUpdateMission createOrUpdateMission) {
        if (createOrUpdateMission == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[createMission] Missing body", 
                "[createMission] CreateOrUpdateMission must be provided");
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
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                        "[createMission] Escape not found", 
                        "[createMission] escape_id: " + createOrUpdateMission.getEscapeId() + " doesn't exist"));
            mission.setEscape(escape);
        }

        if (createOrUpdateMission.getClues() != null && !createOrUpdateMission.getClues().isEmpty()) {
            addClues(createOrUpdateMission.getClues(), mission);
        }

        if (createOrUpdateMission.getMissionOptions() != null) {
            setMissionOptions(createOrUpdateMission, mission);
        }
// TODO players
//        if (createOrUpdateMission.getPlayerMappings() != null) {
//            addPlayers(createOrUpdateMission.getPlayerMappings(), mission);
//        }

        missionRepository.save(mission);

        return missionMapper.missionToMissionDto(mission);
    }
// TODO players
//    private void addPlayers(final List<CreateOrUpdateMissionPlayerMapping> createOrUpdateMissionPlayerMappings, final Mission mission) {
//        final Set<Integer> distinctPlayerIds = createOrUpdateMissionPlayerMappings.stream()
//                .map(CreateOrUpdateMissionPlayerMapping::getPlayerId).collect(Collectors.toSet());
//        ;
//        final List<Player> players = playerRepository.findAllById(distinctPlayerIds);
//        if(players.size() != distinctPlayerIds.size()) {
//            final List<Integer> foundIds = players.stream().map(Player::getId).toList();
//            final List<Integer> missingIds = distinctPlayerIds.stream().filter(id -> !foundIds.contains(id)).toList();
//            throw new TheGameException(HttpStatus.NOT_FOUND,
//                    "Players not found",
//                    "The following ids were not found: " + missingIds + " in the Players database");
//        }
//
//        Map<Integer, Player> playerMap = players.stream().collect(Collectors.toMap(Player::getId, player -> player));
//        for (CreateOrUpdateMissionPlayerMapping createPlayerMapping : createOrUpdateMissionPlayerMappings) {
//            final MissionPlayerMapping missionPlayerMapping = new MissionPlayerMapping();
//            missionPlayerMapping.setPlayer(playerMap.get(createPlayerMapping.getPlayerId()));
//            missionPlayerMapping.setMission(mission);
//            try {
//                missionPlayerMapping.setStatus(MissionStatusEnum.valueOf(createPlayerMapping.getStatus()));
//            } catch (IllegalArgumentException e) {
//                throw new TheGameException(HttpStatus.BAD_REQUEST, "One MissionPlayer status dosen't exist", "The status: " + createPlayerMapping.getStatus() + " doens't exist");
//            }
//            missionPlayerMapping.setStartDate(createPlayerMapping.getStartDate());
//            missionPlayerMapping.setEndDate(createPlayerMapping.getEndDate());
//            mission.addPlayerMapping(missionPlayerMapping);
//        }
//    }

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
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[setMissionType] Invalid mission type", 
                "[setMissionType] mission_type must be STANDARD or CUSTOM, received: " + createOrUpdateMission.getMissionType());
        }
        return mission;
    }

    private void addClues(final List<CreateOrUpdateClue> createOrUpdateClues, final Mission mission) {
        for (CreateOrUpdateClue createOrUpdateClue : createOrUpdateClues) {
            final Clue clue = new Clue();
            clue.setMission(mission);
            clue.setOrder(createOrUpdateClue.getOrder());
            clue.setContent(createOrUpdateClue.getContent());
            clue.setSubOrder(createOrUpdateClue.getSubOrder());
            mission.addClue(clue);
        }
    }

    private static void setMissionOptions(final CreateOrUpdateMission createOrUpdateMission, final Mission mission) {
        final List<MissionOption> missionOptions = new ArrayList<>();
        for (CreateOrUpdateMissionOption createOrUpdateMissionOption : createOrUpdateMission.getMissionOptions()) {
            final MissionOption missionOption = new MissionOption();
            missionOption.setMission(mission);
            if (createOrUpdateMissionOption.getConditions() == null || createOrUpdateMissionOption.getConditions().isEmpty()) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, "[setMissionOptions] Missing condition", "[setMissionOptions] The MissionOption: " + createOrUpdateMissionOption.toString() + "is missing a condition");
            }
            final List<MissionConditionEnum> missionConditionEnums = new ArrayList<>();
            for (String condition : createOrUpdateMissionOption.getConditions()) {
                MissionConditionEnum missionConditionEnum;
                try {
                    missionConditionEnum = MissionConditionEnum.valueOf(condition);
                } catch (IllegalArgumentException e) {
                    throw new TheGameException(HttpStatus.BAD_REQUEST, 
                        "[setMissionOptions] Invalid condition", 
                        "[setMissionOptions] Invalid condition: " + condition);
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
            missionOptions.add(missionOption);
        }
        mission.setOptions(missionOptions);
    }

    public MissionDto updateMission(Integer id, CreateOrUpdateMission createOrUpdateMission) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateMission] Missing id", 
                "[updateMission] mission_id must be provided");
        } else if (createOrUpdateMission == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateMission] Missing body", 
                "[updateMission] CreateOrUpdateMission must be provided");
        }

        log.info("[updateMission] RETREIVING MISSION ID = " + id);

        Mission existingMission = missionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[updateMission] Mission not found", 
                    "[updateMission] mission_id: " + id + " doesn't exist"));
        log.info("[updateMission] MISSION RETRIEVED");

        updateCommonValues(createOrUpdateMission, existingMission);

        if (createOrUpdateMission.getEscapeId() != null) {
            final Escape escape = escapeRepository.findById(createOrUpdateMission.getEscapeId())
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                        "[updateMission] Escape not found", 
                        "[updateMission] escape_id: " + createOrUpdateMission.getEscapeId() + " doesn't exist"));
            existingMission.setEscape(escape);
        }

//        if (createOrUpdateMission.getPlayerMappings() != null) {
//            addPlayers(createOrUpdateMission.getPlayerMappings(), existingMission);
//        }

        // can only create clues. We go through ClueService to add/remove new Clues
        if (createOrUpdateMission.getClues() != null) {
            addClues(createOrUpdateMission.getClues(), existingMission);
        }

        if (createOrUpdateMission.getMissionOptions() != null) {
            updateMissionOptions(createOrUpdateMission, existingMission);
        }

        // TODO: can we update a mission type ?
        if (existingMission instanceof CustomMission) {
            if (!Objects.equals(createOrUpdateMission.getMissionType(), "CUSTOM")) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, 
                    "[updateMission] Type mismatch", 
                    "[updateMission] Cannot change CUSTOM mission to type: " + createOrUpdateMission.getMissionType());
            }
            ((CustomMission) existingMission).setSubOrder(createOrUpdateMission.getSubOrder());
        } else if (existingMission instanceof StandardMission) {
            if (!Objects.equals(createOrUpdateMission.getMissionType(), "STANDARD")) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, 
                    "[updateMission] Type mismatch", 
                    "[updateMission] Cannot change STANDARD mission to type: " + createOrUpdateMission.getMissionType());
            }
            if (createOrUpdateMission.getSuccessRate() != null) {
                ((StandardMission) existingMission).setSuccessRate(createOrUpdateMission.getSuccessRate());
            }
        }

        // TODO add / remove Clues MissionOptions
        // TODO updatePlayers

        missionRepository.save(existingMission);
        final MissionDto missionDto = missionMapper.missionToMissionDto(existingMission);
        return missionDto;
    }

    private static void updateMissionOptions(final CreateOrUpdateMission createOrUpdateMission, final Mission existingMission) {
        for (CreateOrUpdateMissionOption createOrUpdateMissionOption : createOrUpdateMission.getMissionOptions()) {
            final MissionOption missionOption = new MissionOption();
            missionOption.setDescription(createOrUpdateMissionOption.getDescription());
            missionOption.setMission(existingMission);
            if (createOrUpdateMissionOption.getConditions() != null) {
                for (String condition : createOrUpdateMissionOption.getConditions()) {
                    try {
                        MissionConditionEnum missionConditionEnum = MissionConditionEnum.valueOf(condition);
                        missionOption.addCondition(missionConditionEnum);
                    } catch (IllegalArgumentException e) {
                        throw new TheGameException(HttpStatus.BAD_REQUEST, "[updateMissionOptions] Condition not acceptable", "[updateMissionOptions] condition: " + condition + " is not a MissionCondition");
                    }
                }
            }
            if (createOrUpdateMissionOption.getClues() != null) {
                for (CreateOrUpdateClue createOrUpdateClue : createOrUpdateMissionOption.getClues()) {
                    final Clue clue = new Clue();
                    clue.setContent(createOrUpdateClue.getContent());
                    clue.setMissionOption(missionOption);
                    clue.setOrder(createOrUpdateClue.getOrder());
                    clue.setSubOrder(createOrUpdateClue.getSubOrder());
                    missionOption.addClue(clue);
                }
            }
            existingMission.addMissionOption(missionOption);
        }
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

    /**
     * Check if the order of a standard mission is already taken by another standard mission in the same escape
     * @param mission Mission to check
     * @return true if order not used (is valid), false if already used
     */
    private boolean validateStandardMissionOrder(StandardMission mission) {
        log.info("[validateStandardMissionOrder] Validating order {} for standard mission_id: {} in escape_id: {}", 
            mission.getOrder(), mission.getId(), 
            mission.getEscape() != null ? mission.getEscape().getId() : "null");

        if (mission.getEscape() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[validateStandardMissionOrder] Mission not linked to an escape",
                "[validateStandardMissionOrder] mission_id: " + mission.getId() + " is not linked to any escape");
        }

        List<Integer> existingMissionIds = missionRepository.findStandardMissionIdsWithSameOrderInEscape(
            mission.getEscape().getId(),
            mission.getOrder(),
            mission.getId()
        );

        if (!existingMissionIds.isEmpty()) {
            log.warn("[validateStandardMissionOrder] Order {} is already taken by standard mission_ids: {} in escape_id: {}", 
                mission.getOrder(), existingMissionIds, mission.getEscape().getId());
        }

        return existingMissionIds.isEmpty();
    }

    public List<Mission> getOrderedMissions(List<Mission> missions) {
        if (missions == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[getOrderedMissions] Missing missions",
                "[getOrderedMissions] missions must be provided");
        }

        log.info("[getOrderedMissions] Ordering {} missions", missions.size());

        // Group missions by order
        Map<Integer, List<Mission>> missionsByOrder = missions.stream()
                .collect(Collectors.groupingBy(Mission::getOrder));

        List<Mission> orderedMissions = missionsByOrder.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> {
                    List<Mission> missionsWithSameOrder = entry.getValue();
                    
                    // Sort standard missions first, then custom missions by subOrder
                    return missionsWithSameOrder.stream()
                            .sorted((m1, m2) -> {
                                if (m1 instanceof StandardMission && m2 instanceof CustomMission) {
                                    return -1;
                                } else if (m1 instanceof CustomMission && m2 instanceof StandardMission) {
                                    return 1;
                                } else if (m1 instanceof CustomMission && m2 instanceof CustomMission) {
                                    return ((CustomMission) m1).getSubOrder()
                                            .compareTo(((CustomMission) m2).getSubOrder());
                                }
                                return 0;
                            });
                })
                .collect(Collectors.toList());

        log.info("[getOrderedMissions] Successfully ordered {} missions", orderedMissions.size());
        return orderedMissions;
    }

    public Integer getNextAvailableSubOrder(Integer order) {
        if (order == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[getNextAvailableSubOrder] Missing order",
                "[getNextAvailableSubOrder] mission_order must be provided");
        }

        log.info("[getNextAvailableSubOrder] Finding next available sub-order for order: {}", order);

        Integer nextSubOrder = missionRepository.findNextAvailableSubOrderForOrder(order)
            .orElse(0);

        log.info("[getNextAvailableSubOrder] Next available sub-order for order {}: {}", order, nextSubOrder);
        return nextSubOrder;
    }

    public Integer getNextAvailableOrder(Integer escapeId) {
        if (escapeId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[getNextAvailableOrder] Missing escape id",
                "[getNextAvailableOrder] escape_id must be provided");
        }

        // Verify escape exists
        escapeRepository.findById(escapeId)
            .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                "[getNextAvailableOrder] Escape not found",
                "[getNextAvailableOrder] escape_id: " + escapeId + " not found"));

        log.info("[getNextAvailableOrder] Finding next available order for escape_id: {}", escapeId);

        Integer nextOrder = missionRepository.findNextAvailableOrderForEscape(escapeId)
            .orElse(0);

        log.info("[getNextAvailableOrder] Next available order for escape_id {}: {}", escapeId, nextOrder);
        return nextOrder;
    }

}