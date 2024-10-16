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
import pedribault.game.model.dto.MissionOptionDto;
import pedribault.game.repository.ClueRepository;
import pedribault.game.repository.MissionOptionRepository;
import pedribault.game.repository.MissionRepository;

import java.util.List;

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

        return missionOptionMapper.missionOptionToMissionOptionDto(missionOption);
    }

    private static void addConditionsAndClues(final CreateOrUpdateMissionOption createOrUpdateMissionOption, final MissionOption missionOption) {
        addConditions(createOrUpdateMissionOption.getConditions(), missionOption);
        addClues(createOrUpdateMissionOption.getClues(), missionOption);
    }

    private static void addClues(final List<CreateOrUpdateClue> clues, final MissionOption missionOption) {
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

        if (missionId != null) {
            final Mission mission = missionRepository.findById(missionId)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission not found", "Mission id: " + id + " doesn't exist in the Missions database"));
            missionOption.setMission(mission);
        }

        if (createOrUpdateMissionOption != null) {
            addConditionsAndClues(createOrUpdateMissionOption, missionOption);
            missionOption.setDescription(createOrUpdateMissionOption.getDescription());
        }

        missionOptionRepository.save(missionOption);

        return missionOptionMapper.missionOptionToMissionOptionDto(missionOption);
    }

    public MissionOptionDto addClues(Integer id, List<CreateOrUpdateClue> createOrUpdateClues) {
        final MissionOption missionOption = missionOptionRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Mission Option not found", "Mission Option id: " + id + " doesn't exist in the Mission_Option database"));
        addClues(createOrUpdateClues, missionOption);
        missionOptionRepository.save(missionOption);
        return missionOptionMapper.missionOptionToMissionOptionDto(missionOption);
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
