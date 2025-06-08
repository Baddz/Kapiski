package pedribault.game.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pedribault.game.enums.MissionConditionEnum;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.MissionMapper;
import pedribault.game.mappers.MissionOptionMapper;
import pedribault.game.model.*;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateClue;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateMissionOption;
import pedribault.game.model.dto.CreateOrUpdate.UpdateClue;
import pedribault.game.model.dto.CreateOrUpdate.UpdateMissionOption;
import pedribault.game.model.dto.MissionDto;
import pedribault.game.model.dto.MissionOptionDto;
import pedribault.game.repository.ClueRepository;
import pedribault.game.repository.MissionOptionRepository;
import pedribault.game.repository.MissionRepository;
import pedribault.game.utils.ObjectHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Map;
import java.util.HashSet;

@Slf4j
@Service
public class MissionOptionService {

    @Autowired
    private MissionOptionRepository missionOptionRepository;
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private MissionOptionMapper missionOptionMapper;
    @Autowired
    private ClueService clueService;
    @Autowired
    private ClueRepository clueRepository;
    @Autowired
    private MissionMapper missionMapper;
    @Autowired
    private ObjectHandler objectHandler;
    @Autowired
    private EntityManager entityManager;

    public List<MissionOptionDto> getMissionOptions() {
        final List<MissionOption> missionOptions = missionOptionRepository.findAll();
        return missionOptionMapper.missionOptionsToMissionOptionDtos(missionOptions);
    }

    public List<MissionOptionDto> getMissionOptionsByMissionId(Integer missionId) {
        if (missionId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[getMissionOptionsByMissionId] Missing mission id", 
                "[getMissionOptionsByMissionId] mission_id must be provided");
        }
        final List<MissionOption> missionOptions = missionOptionRepository.findByMissionId(missionId);
        return missionOptionMapper.missionOptionsToMissionOptionDtos(missionOptions);
    }

    public MissionOptionDto getMissionOptionsById(Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[getMissionOptionsById] Missing id", 
                "[getMissionOptionsById] mission_option_id must be provided");
        }
        final MissionOption missionOption = missionOptionRepository.findById(id)
            .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                "[getMissionOptionsById] Mission option not found", 
                "[getMissionOptionsById] mission_option_id: " + id + " not found"));
        return missionOptionMapper.missionOptionToMissionOptionDto(missionOption);
    }

    public MissionOptionDto createMissionOption(CreateOrUpdateMissionOption createOrUpdateMissionOption, Integer missionId) {
        if (createOrUpdateMissionOption == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[createMissionOption] Missing body", 
                "[createMissionOption] CreateOrUpdateMissionOption must be provided");
        }
        if (missionId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[createMissionOption] Missing mission id", 
                "[createMissionOption] mission_id must be provided");
        }

        final Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[createMissionOption] Mission not found", 
                    "[createMissionOption] mission_id: " + missionId + " not found"));
        final MissionOption missionOption = new MissionOption();

        addConditionsAndClues(createOrUpdateMissionOption, missionOption);
        missionOption.setMission(mission);
        missionOption.setDescription(createOrUpdateMissionOption.getDescription());
        missionOptionRepository.save(missionOption);

        return missionOptionMapper.missionOptionToMissionOptionDto(missionOption);
    }

    private static void addConditionsAndClues(final CreateOrUpdateMissionOption createOrUpdateMissionOption, final MissionOption missionOption) {
        addConditions(createOrUpdateMissionOption.getConditions(), missionOption);
        addClues(createOrUpdateMissionOption.getClues(), missionOption);
    }

    private static void addClues(final List<UpdateClue> clues, final MissionOption missionOption) {
        for (CreateOrUpdateClue createOrUpdateClue : clues) {
            if (createOrUpdateClue == null) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, 
                    "[addClues] Missing clue", 
                    "[addClues] clue must be provided");
            }
            if (createOrUpdateClue.getContent() == null || createOrUpdateClue.getOrder() == null) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, 
                    "[addClues] Missing clue properties", 
                    "[addClues] clue content and order must be provided");
            }

            final Clue clue = new Clue();
            clue.setContent(createOrUpdateClue.getContent());
            clue.setOrder(createOrUpdateClue.getOrder());
            clue.setSubOrder(createOrUpdateClue.getSubOrder());
            clue.setMissionOption(missionOption);

            missionOption.addClue(clue);
        }
    }

    private static void addConditions(final List<String> conditions, final MissionOption missionOption) {
        for (String condition : conditions) {
            try {
                missionOption.addCondition(MissionConditionEnum.valueOf(condition));
            } catch (IllegalArgumentException e) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, 
                    "[addConditions] Invalid condition", 
                    "[addConditions] condition: " + condition + " is not valid");
            }
        }
    }

    public MissionOptionDto updateMissionOption(Integer id, Integer missionId, CreateOrUpdateMissionOption createOrUpdateMissionOption) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateMissionOption] Missing id", 
                "[updateMissionOption] mission_option_id must be provided");
        }
        if (createOrUpdateMissionOption == null && missionId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateMissionOption] Missing parameters", 
                "[updateMissionOption] mission_id or CreateOrUpdateMissionOption must be provided");
        }

        final MissionOption missionOption = missionOptionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[updateMissionOption] Mission option not found", 
                    "[updateMissionOption] mission_option_id: " + id + " not found"));
        AtomicBoolean updated = new AtomicBoolean(false);
        if (missionId != null) {
            final Mission mission = missionRepository.findById(missionId)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                        "[updateMissionOption] Mission not found", 
                        "[updateMissionOption] mission_id: " + missionId + " not found"));
            if (!mission.equals(missionOption.getMission())) {
                missionOption.setMission(mission);
                updated.set(true);
            }
        }

        if (createOrUpdateMissionOption != null) {
            updateMissionOption(missionOption, createOrUpdateMissionOption, updated);
        }

        if (updated.get()) {
            missionOptionRepository.save(missionOption);
        }

        return missionOptionMapper.missionOptionToMissionOptionDto(missionOption);
    }

    @Transactional
    public MissionDto updateMissionOptions(Integer missionId, List<UpdateMissionOption> updateMissionOptions) {
        if (updateMissionOptions == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateMissionOptions] Missing options", 
                "[updateMissionOptions] mission_options must be provided");
        }
        
        final Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[updateMissionOptions] Mission not found", 
                    "[updateMissionOptions] mission_id: " + missionId + " not found"));

        // Validate all mission option IDs exist before making any changes
        validateMissionOptionIds(updateMissionOptions);

        // Initialize options list if null
        if (mission.getOptions() == null) {
            mission.setOptions(new ArrayList<>());
        }

        // Update mission options
        boolean wasUpdated = updateMissionOptionsList(mission, updateMissionOptions);

        // Save if changes were made
        if (wasUpdated) {
            if (mission instanceof StandardMission) {
                log.info("[updateMissionOptions] Saving StandardMission: " + mission);
                missionRepository.save((StandardMission) mission);
            } else if (mission instanceof CustomMission) {
                log.info("[updateMissionOptions] Saving CustomMission: " + mission);
                missionRepository.save((CustomMission) mission);
            } else {
                throw new TheGameException(HttpStatus.BAD_REQUEST, 
                    "[updateMissionOptions] Invalid mission type", 
                    "[updateMissionOptions] mission must be StandardMission or CustomMission");
            }
        }

        return missionMapper.missionToMissionDto(mission);
    }

    /**
     * Updates the list of mission options for a mission
     * @param mission The mission whose options are being updated
     * @param updateMissionOptions The new list of mission options
     * @return true if any changes were made
     */
    private boolean updateMissionOptionsList(Mission mission, List<UpdateMissionOption> updateMissionOptions) {
        boolean updated = false;
        List<MissionOption> currentOptions = mission.getOptions();
        Map<Integer, MissionOption> existingOptionsMap = currentOptions.stream()
                .collect(Collectors.toMap(MissionOption::getId, opt -> opt));
        
        // Track which options should remain
        Set<Integer> newOptionIds = updateMissionOptions.stream()
                .map(UpdateMissionOption::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Remove options that are no longer needed
        if (currentOptions.removeIf(option -> 
                option.getId() != null && !newOptionIds.contains(option.getId()))) {
            updated = true;
        }

        // Update or create options
        for (UpdateMissionOption updateOption : updateMissionOptions) {
            if (updateOption.getId() != null) {
                // Update existing option
                MissionOption existingOption = existingOptionsMap.get(updateOption.getId());
                if (existingOption != null) {
                    if (updateExistingMissionOption(existingOption, updateOption)) {
                        updated = true;
                    }
                }
            } else {
                // Create new option
                MissionOption newOption = createMissionOption(updateOption);
                newOption.setMission(mission);
                currentOptions.add(newOption);
                updated = true;
            }
        }

        return updated;
    }

    /**
     * Updates an existing mission option with new data
     * @param existingOption The existing mission option to update
     * @param updateOption The new data
     * @return true if any changes were made
     */
    private boolean updateExistingMissionOption(MissionOption existingOption, UpdateMissionOption updateOption) {
        boolean updated = false;

        // Update description if changed
        if (!Objects.equals(existingOption.getDescription(), updateOption.getDescription())) {
            existingOption.setDescription(updateOption.getDescription());
            updated = true;
        }

        // Update conditions
        if (updateConditionsList(existingOption, updateOption.getConditions())) {
            updated = true;
        }

        // Update clues
        if (updateMissionOptionClues(existingOption, updateOption.getClues())) {
            updated = true;
        }

        return updated;
    }

    /**
     * Updates the list of clues for a mission option
     * @param missionOption The mission option whose clues are being updated
     * @param newClues The new list of clues
     * @return true if any changes were made
     */
    private boolean updateMissionOptionClues(MissionOption missionOption, List<? extends UpdateClue> newClues) {
        if (newClues == null) {
            return false;
        }

        boolean updated = false;
        List<Clue> currentClues = missionOption.getClues();
        Map<Integer, Clue> existingCluesMap = currentClues.stream()
                .collect(Collectors.toMap(Clue::getId, clue -> clue));

        // Track which clues should remain
        Set<Integer> newClueIds = newClues.stream()
                .map(UpdateClue::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Remove clues that are no longer needed
        if (currentClues.removeIf(clue -> 
                Objects.nonNull(clue.getId()) && !newClueIds.contains(clue.getId()))) {
            updated = true;
        }

        // Update or create clues
            for (UpdateClue updateClue : newClues) {
            if (updateClue.getId() != null) {
                // Update existing clue
                Clue existingClue = existingCluesMap.get(updateClue.getId());
                if (existingClue != null) {
                    if (updateExistingClue(existingClue, updateClue)) {
                        updated = true;
                    }
                }
            } else {
                // Create new clue
                Clue newClue = createClueFromUpdate(updateClue);
                newClue.setMissionOption(missionOption);
                currentClues.add(newClue);
                updated = true;
            }
        }

        // Sort clues by order and subOrder
        if (updated) {
            currentClues.sort(Comparator.comparing(Clue::getOrder)
                    .thenComparing(clue -> Objects.requireNonNullElse(clue.getSubOrder(), 0)));
        }

        return updated;
    }

    /**
     * Updates an existing clue with new data
     * @param existingClue The existing clue to update
     * @param updateClue The new data
     * @return true if any changes were made
     */
    private boolean updateExistingClue(Clue existingClue, UpdateClue updateClue) {
        boolean updated = false;

        if (!Objects.equals(existingClue.getOrder(), updateClue.getOrder())) {
            existingClue.setOrder(updateClue.getOrder());
            updated = true;
        }
        if (!Objects.equals(existingClue.getSubOrder(), updateClue.getSubOrder())) {
            existingClue.setSubOrder(updateClue.getSubOrder());
            updated = true;
        }
        if (!Objects.equals(existingClue.getContent(), updateClue.getContent())) {
            existingClue.setContent(updateClue.getContent());
            updated = true;
        }

        return updated;
    }

    /**
     * Updates the list of conditions for a mission option
     * @param missionOption The mission option whose conditions are being updated
     * @param newConditions The new list of conditions
     * @return true if any changes were made
     */
    private boolean updateConditionsList(MissionOption missionOption, List<String> newConditions) {
        if (newConditions == null) {
            return false;
        }

        Set<MissionConditionEnum> updatedConditions = newConditions.stream()
                .map(cond -> {
                    try {
                        return MissionConditionEnum.valueOf(cond);
                    } catch (IllegalArgumentException e) {
                        throw new TheGameException(HttpStatus.BAD_REQUEST, 
                            "[updateConditionsList] Invalid condition", 
                            "[updateConditionsList] mission_condition: " + cond + " is not a valid MissionConditionEnum");
                    }
                }).collect(Collectors.toSet());

        List<MissionConditionEnum> currentConditions = missionOption.getConditions();
        if (!new HashSet<>(currentConditions).equals(updatedConditions)) {
            missionOption.setConditions(new ArrayList<>(updatedConditions));
            return true;
        }

        return false;
    }

    /**
     * Creates a new mission option from update data
     */
    private MissionOption createMissionOption(UpdateMissionOption updateMissionOption) {
        final MissionOption missionOption = new MissionOption();
        missionOption.setDescription(updateMissionOption.getDescription());

        // Add conditions
        if (updateMissionOption.getConditions() != null) {
        for (String condition : updateMissionOption.getConditions()) {
            try {
                missionOption.addCondition(MissionConditionEnum.valueOf(condition));
            } catch (IllegalArgumentException e) {
                    throw new TheGameException(HttpStatus.BAD_REQUEST, 
                        "[createMissionOption] Invalid condition", 
                        "[createMissionOption] mission_condition: " + condition + " is not a valid MissionConditionEnum");
                }
            }
        }

        // Add clues
        if (updateMissionOption.getClues() != null) {
            for (UpdateClue updateClue : updateMissionOption.getClues()) {
                Clue clue = createClueFromUpdate(updateClue);
                missionOption.addClue(clue);
            }
        }

        return missionOption;
    }

    /**
     * Creates a new clue from update data or retrieves an existing one
     */
    private Clue createClueFromUpdate(UpdateClue updateClue) {
        if (updateClue.getId() != null) {
            return clueRepository.findById(updateClue.getId())
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[createClueFromUpdate] Clue not found", 
                    "[createClueFromUpdate] clue_id: " + updateClue.getId() + " not found"));
        }

        final Clue clue = new Clue();
        clue.setContent(updateClue.getContent());
        clue.setOrder(updateClue.getOrder());
        clue.setSubOrder(updateClue.getSubOrder());
        return clue;
    }

    /**
     * Validates that all mission option IDs exist in the database
     */
    private void validateMissionOptionIds(List<UpdateMissionOption> updateMissionOptions) {
        List<UpdateMissionOption> optionsWithIds = updateMissionOptions.stream()
                .filter(umo -> umo.getId() != null)
                .collect(Collectors.toList());
        
        List<Integer> requestedIds = optionsWithIds.stream()
                .map(UpdateMissionOption::getId)
                .collect(Collectors.toList());
        
        List<MissionOption> foundOptions = missionOptionRepository.findAllById(requestedIds);
        Set<Integer> foundIds = foundOptions.stream()
                .map(MissionOption::getId)
                .collect(Collectors.toSet());
        
        List<Integer> missingIds = requestedIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());
        
        if (!missingIds.isEmpty()) {
            throw new TheGameException(HttpStatus.NOT_FOUND, 
                "[validateMissionOptionIds] MissionOptions not found", 
                "[validateMissionOptionIds] mission options ids: " + missingIds + " don't exist in the Mission_Option database");
        }
    }

    public MissionOptionDto removeClues(Integer id, List<Integer> clueIds) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[removeClues] Missing id", 
                "[removeClues] mission_option_id must be provided");
        }
        if (clueIds == null || clueIds.isEmpty()) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[removeClues] Missing clue ids", 
                "[removeClues] clue_ids must be provided");
        }

        final MissionOption missionOption = missionOptionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[removeClues] Mission option not found", 
                    "[removeClues] mission_option_id: " + id + " not found"));

        final List<Clue> clues = clueRepository.findAllById(clueIds);
        if (clues.size() != clueIds.size()) {
            final List<Integer> foundIds = clues.stream().map(Clue::getId).toList();
            final List<Integer> missingIds = clueIds.stream().filter(cId -> !foundIds.contains(cId)).toList();
            throw new TheGameException(HttpStatus.NOT_FOUND, 
                "[removeClues] Clues not found", 
                "[removeClues] clue_ids not found: " + missingIds);
        }

        clues.forEach(clue -> {
            if (!clue.getMissionOption().equals(missionOption)) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, 
                    "[removeClues] Invalid mission_option", 
                    "[removeClues] clue_id: " + clue.getId() + " not linked to mission_option_id: " + id + " but to mission_option_id: " + clue.getMissionOption().getId());
            }
            missionOption.removeClue(clue);
            clueRepository.delete(clue);
        });

        missionOptionRepository.save(missionOption);
        return missionOptionMapper.missionOptionToMissionOptionDto(missionOption);
    }

    public MissionDto removeMissionOption(Integer missionId, Integer id) {
        if (missionId == null || id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[removeMissionOption] Missing parameters", 
                "[removeMissionOption] mission_id and mission_option_id must be provided");
        }

        final Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[removeMissionOption] Mission not found", 
                    "[removeMissionOption] mission_id: " + missionId + " not found"));

        final MissionOption missionOption = missionOptionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[removeMissionOption] Mission option not found", 
                    "[removeMissionOption] mission_option_id: " + id + " not found"));

        if (!missionOption.getMission().equals(mission)) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[removeMissionOption] Invalid mission", 
                "[removeMissionOption] mission_option_id: " + id + " not linked to mission_id: " + missionId + " but to mission_id: " + missionOption.getMission().getId());
        }

        mission.removeOption(missionOption);
        missionOptionRepository.delete(missionOption);
        missionRepository.save(mission);

        return missionMapper.missionToMissionDto(mission);
    }

    private void updateMissionOption(MissionOption missionOption, CreateOrUpdateMissionOption createOrUpdateMissionOption, AtomicBoolean updated) {
        if (!Objects.equals(missionOption.getDescription(), createOrUpdateMissionOption.getDescription())) {
            missionOption.setDescription(createOrUpdateMissionOption.getDescription());
            updated.set(true);
        }

        // Update conditions
        if (updateConditionsList(missionOption, createOrUpdateMissionOption.getConditions())) {
            updated.set(true);
        }

        // Update clues
        if (updateMissionOptionClues(missionOption, createOrUpdateMissionOption.getClues())) {
            updated.set(true);
        }

        entityManager.flush();
        entityManager.refresh(missionOption);
    }
}

// TODO review all service for refactor & final testing
