package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pedribault.game.enums.MissionConditionEnum;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.MissionOptionMapper;
import pedribault.game.model.Clue;
import pedribault.game.model.Mission;
import pedribault.game.model.MissionOption;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateClue;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateMissionOption;
import pedribault.game.model.dto.CreateOrUpdate.UpdateClue;
import pedribault.game.model.dto.MissionOptionDto;
import pedribault.game.repository.ClueRepository;
import pedribault.game.repository.MissionOptionRepository;
import pedribault.game.repository.MissionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<MissionOptionDto> getMissionOptions() {
        final List<MissionOption> missionOptions = missionOptionRepository.findAll();
        return missionOptionMapper.missionOptionsToMissionOptionDtos(missionOptions);
    }

    public List<MissionOptionDto> getMissionOptionsByMissionId(Integer missionId) {
        if (missionId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id is required", "Id is null");
        }
        final List<MissionOption> missionOptions = missionOptionRepository.findByMissionId(missionId);
        return missionOptionMapper.missionOptionsToMissionOptionDtos(missionOptions);
    }

    public MissionOptionDto getMissionOptionsById(Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id is required", "Id is null");
        }
        final MissionOption missionOption = missionOptionRepository.findById(id).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "MissionOption not found", "Id: " + id + " doesn't exist in the Mission_Options database"));
        return missionOptionMapper.missionOptionToMissionOptionDto(missionOption);
    }

    public MissionOptionDto createMissionOption(CreateOrUpdateMissionOption createOrUpdateMissionOption, Integer missionId) {
        if (createOrUpdateMissionOption == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Body is required", "Body is null");
        }
        if (missionId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Mission id is required", "Mission id is null");
        }

        final Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission not found", "Mission id: " + missionId + " doesn't exist in the Missions database"));
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
                throw new TheGameException(HttpStatus.BAD_REQUEST, "Clue is null", "A clue is required");
            }
            if (createOrUpdateClue.getContent() == null || createOrUpdateClue.getOrder() == null) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, "Missing order or content of the clue", "Order and content of the clue need to be specified in the body");
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
                throw new TheGameException(HttpStatus.BAD_REQUEST, "Invalid MissionOption condition", "MissionOption condition: " + condition + " doesn't exist");
            }
        }
    }

    public MissionOptionDto updateMissionOption(Integer id, Integer missionId, CreateOrUpdateMissionOption createOrUpdateMissionOption) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id not provided", "Id is needed");
        }
        if (createOrUpdateMissionOption == null && missionId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "No missionId nor MissionOption provided", "A missionId or a body is required");
        }

        final MissionOption missionOption = missionOptionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission Option not found", "Mission Option id: " + id + " doesn't exist in the Mission_Option database"));
        boolean updated = false;
        if (missionId != null) {
            final Mission mission = missionRepository.findById(missionId)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission not found", "Mission id: " + id + " doesn't exist in the Missions database"));
            if (!mission.equals(missionOption.getMission())) {
                missionOption.setMission(mission);
                updated = true;
            }
        }

        if (createOrUpdateMissionOption != null) {
            updateConditions(createOrUpdateMissionOption, missionOption, updated);
            updateClues(createOrUpdateMissionOption, missionOption, updated);
            if (createOrUpdateMissionOption.getDescription() != null && !createOrUpdateMissionOption.getDescription().equals(missionOption.getDescription())) {
                missionOption.setDescription(createOrUpdateMissionOption.getDescription());
                updated = true;
            }
        }

        if (updated) {
            missionOptionRepository.save(missionOption);
        }

        return missionOptionMapper.missionOptionToMissionOptionDto(missionOption);
    }

    private void updateClues(final CreateOrUpdateMissionOption createOrUpdateMissionOption, final MissionOption missionOption, boolean updated) {
        if (createOrUpdateMissionOption.getClues() != null) {
            final List<UpdateClue> newClues = createOrUpdateMissionOption.getClues().stream().filter(c -> c.getId() == null).toList();
            final List<UpdateClue> updateClueDtos = createOrUpdateMissionOption.getClues().stream().filter(c -> c.getId() != null).toList();
            final Set<Integer> updateClueIdSet = updateClueDtos.stream().map(UpdateClue::getId).collect(Collectors.toSet());
            final List<Clue> updateClues = clueRepository.findAllById(updateClueIdSet);
            // check if all exist
            if (updateClues.size() != updateClueIdSet.size()) {
                final List<Integer> foundIds = updateClues.stream().map(Clue::getId).toList();
                final List<Integer> missingIds = updateClueIdSet.stream().filter(foundIds::contains).toList();
                throw new TheGameException(HttpStatus.NOT_FOUND,
                "Clues not found",
                "The following ids were not found: " + missingIds + " in the Clues database");
            }
            final List<Clue> updatedClues = new ArrayList<>();
            // add
            for (Clue clue : updateClues) {
                if (!missionOption.getClues().contains(clue)) {
                    missionOption.addClue(clue);
                    clue.setMissionOption(missionOption);
                    clue.setMission(null);
                    updatedClues.add(clue);
                    updated = true;
                }
            }
            clueRepository.saveAll(updatedClues);
            // remove
            for (Clue clue : missionOption.getClues()) {
                if (!updateClues.contains(clue)) {
                    missionOption.removeClue(clue);
                    updated = true;
                }
            }

            // create
            for (UpdateClue newClueDto : newClues) {
                Clue newClue = new Clue();
                newClue.setContent(newClueDto.getContent());
                newClue.setOrder(newClueDto.getOrder());
                newClue.setSubOrder(newClueDto.getSubOrder());
                missionOption.addClue(newClue);
                updated = true;
            }
        }
    }

    private static void updateConditions(final CreateOrUpdateMissionOption createOrUpdateMissionOption, final MissionOption missionOption, boolean updated) {
        if (createOrUpdateMissionOption.getConditions() != null) {
            final List<MissionConditionEnum> missionConditionEnums = new ArrayList<>();
            for (String condition : createOrUpdateMissionOption.getConditions()) {
                try {
                    final MissionConditionEnum missionConditionEnum = MissionConditionEnum.valueOf(condition);
                    missionConditionEnums.add(missionConditionEnum);
                } catch (IllegalArgumentException e) {
                    throw new TheGameException(HttpStatus.BAD_REQUEST, "Invalid Mission Condition", "Mission Condition: " + condition + " doesn't exist");
                }
            }
            // add
            for (MissionConditionEnum missionConditionEnum : missionConditionEnums) {
                if (!missionOption.getConditions().contains(missionConditionEnum)) {
                    missionOption.addCondition(missionConditionEnum);
                    updated = true;
                }
            }
            // remove
            for (MissionConditionEnum missionConditionEnum : missionOption.getConditions()) {
                if (!missionConditionEnums.contains(missionConditionEnum)) {
                    missionOption.removeCondition(missionConditionEnum);
                    updated = true;
                }
            }
        }
    }

    public MissionOptionDto removeClues(Integer id, List<Integer> clueIds) {
        final MissionOption missionOption = missionOptionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission Option not found", "Mission Option id: " + id + " doesn't exist in the Mission_Option database"));

        final List<Clue> clues = clueRepository.findAllById(clueIds);
        log.info("RETRIEVED CLUES: [" + clues.stream().map(Clue::getId).toString() + "]");
        for (Clue clue : clues) {
            if (!missionOption.getClues().contains(clue)) {
                log.info("Clue [ID]=[" + clue.getId() + "] not a clue from Mission Option [ID]=[" + id + "]. Clue not removed");
            } else {
                missionOption.removeClue(clue);
            }
        }

        return missionOptionMapper.missionOptionToMissionOptionDto(missionOption);
    }
}

// TODO separation create / update clues, missionOption
