package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateClue;
import pedribault.game.model.dto.ClueDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.ClueMapper;
import pedribault.game.model.Clue;
import pedribault.game.model.Mission;
import pedribault.game.model.MissionOption;
import pedribault.game.repository.ClueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pedribault.game.repository.MissionOptionRepository;
import pedribault.game.repository.MissionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClueService {

    @Autowired
    private ClueRepository clueRepository;
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private MissionOptionRepository missionOptionRepository;
    @Autowired
    private ClueMapper clueMapper;

    // TODO Update/create clue with RequestParameter missionId / missionOptionId

    public List<ClueDto> getClues() {
        final List<Clue> clues = clueRepository.findAll();
        return clues.stream()
                .map(clueMapper::clueToClueDto)
                .collect(Collectors.toList());
    }

    public ClueDto getClueById(Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[getClueById] Missing id", 
                "[getClueById] clue_id must be provided");
        }

        Clue clue = clueRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[getClueById] Clue not found", 
                    "[getClueById] clue_id: " + id + " doesn't exist"));
        return clueMapper.clueToClueDto(clue);
    }

    public List<ClueDto> getCluesByMission(Integer missionId) {
        if (missionId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[getCluesByMission] Missing mission id",
                "[getCluesByMission] mission_id must be provided");
        }

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                    "[getCluesByMission] Mission not found",
                    "[getCluesByMission] mission_id: " + missionId + " doesn't exist"));

        return clueRepository.findByMissionOrderByOrderAscSubOrderAsc(mission)
                .stream()
                .map(clueMapper::clueToClueDto)
                .collect(Collectors.toList());
    }

    public List<ClueDto> getCluesByMissionAndOrder(Integer missionId, Integer order) {
        if (missionId == null || order == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[getCluesByMissionAndOrder] Missing parameters",
                "[getCluesByMissionAndOrder] mission_id and order must be provided");
        }

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                    "[getCluesByMissionAndOrder] Mission not found",
                    "[getCluesByMissionAndOrder] mission_id: " + missionId + " doesn't exist"));

        return clueRepository.findByMissionAndOrderOrderBySubOrderAsc(mission, order)
                .stream()
                .map(clueMapper::clueToClueDto)
                .collect(Collectors.toList());
    }

    public ClueDto createClue(final CreateOrUpdateClue createOrUpdateClue) {
        if (createOrUpdateClue == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[createClue] Missing body", 
                "[createClue] CreateOrUpdateClue must be provided");
        }
        if (createOrUpdateClue.getContent() == null || createOrUpdateClue.getOrder() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[createClue] Missing required fields", 
                "[createClue] content and order must be provided");
        }
        if (createOrUpdateClue.getMissionId() == null && createOrUpdateClue.getMissionOptionId() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[createClue] Missing parent reference", 
                "[createClue] mission_id or mission_option_id must be provided");
        }

        final Clue clue = new Clue();
        clue.setContent(createOrUpdateClue.getContent());
        clue.setOrder(createOrUpdateClue.getOrder());
        clue.setSubOrder(createOrUpdateClue.getSubOrder());

        final Integer missionId = createOrUpdateClue.getMissionId();
        if (missionId != null) {
            final Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[createClue] Mission not found", 
                    "[createClue] mission_id: " + missionId + " doesn't exist"));
            clue.setMission(mission);
        }

        final Integer missionOptionId = createOrUpdateClue.getMissionOptionId();
        if (missionOptionId != null) {
            final MissionOption missionOption = missionOptionRepository.findById(missionOptionId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[createClue] MissionOption not found", 
                    "[createClue] mission_option_id: " + missionOptionId + " doesn't exist"));
            clue.setMissionOption(missionOption);
        }

        clueRepository.save(clue);
        return clueMapper.clueToClueDto(clue);
    }

    @Transactional
    public List<ClueDto> createClues(List<CreateOrUpdateClue> createOrUpdateClues) {
        if (createOrUpdateClues == null || createOrUpdateClues.isEmpty()) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[createClues] Missing payload",
                "[createClues] List of CreateOrUpdateClue must be provided and non-empty");
        }

        return createOrUpdateClues.stream()
                .map(this::createClue)
                .collect(Collectors.toList());
    }

    public ClueDto updateClue(Integer id, CreateOrUpdateClue createOrUpdateClue) {
        if (createOrUpdateClue == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateClue] Missing body", 
                "[updateClue] CreateOrUpdateClue must be provided");
        } else if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateClue] Missing id", 
                "[updateClue] clue_id must be provided");
        }

        Clue existingClue = clueRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[updateClue] Clue not found", 
                    "[updateClue] clue_id: " + id + " doesn't exist"));

        if (createOrUpdateClue.getOrder() != null) {
            existingClue.setOrder(createOrUpdateClue.getOrder());
        }
        if (createOrUpdateClue.getSubOrder() != null) {
            existingClue.setSubOrder(createOrUpdateClue.getSubOrder());
        }
        if (createOrUpdateClue.getContent() != null) {
            existingClue.setContent(createOrUpdateClue.getContent());
        }

        final Integer missionId = createOrUpdateClue.getMissionId();
        // missionId == null --> no modification
        if (missionId != null) {
            final Mission mission = missionRepository.findById(missionId)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                        "[updateClue] Mission not found", 
                        "[updateClue] mission_id: " + missionId + " doesn't exist"));
            existingClue.setMission(mission);
        }

        final Integer missionOptionId = createOrUpdateClue.getMissionOptionId();
        if (missionOptionId != null) {
            final MissionOption missionOption = missionOptionRepository.findById(missionOptionId)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                        "[updateClue] MissionOption not found", 
                        "[updateClue] mission_option_id: " + missionOptionId + " doesn't exist"));
            existingClue.setMissionOption(missionOption);
        }

        clueRepository.save(existingClue);
        return clueMapper.clueToClueDto(existingClue);
    }

    @Transactional
    public void deleteClue(Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[deleteClue] Missing id",
                "[deleteClue] clue_id must be provided");
        }

        Clue clue = clueRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                    "[deleteClue] Clue not found",
                    "[deleteClue] clue_id: " + id + " doesn't exist"));

        clueRepository.delete(clue);
    }

    @Transactional
    public void deleteCluesByMission(Integer missionId) {
        if (missionId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[deleteCluesByMission] Missing id",
                "[deleteCluesByMission] mission_id must be provided");
        }

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                    "[deleteCluesByMission] Mission not found",
                    "[deleteCluesByMission] mission_id: " + missionId + " doesn't exist"));

        clueRepository.deleteByMission(mission);
    }
}
