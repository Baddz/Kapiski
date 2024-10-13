package pedribault.game.mappers;

import org.springframework.stereotype.Component;
import pedribault.game.enums.MissionConditionEnum;
import pedribault.game.enums.MissionTypeEnum;
import pedribault.game.model.*;
import pedribault.game.model.dto.summary.*;

import java.util.ArrayList;
import java.util.List;

@Component
public class ToSummaryMapper {

    public MissionSummary missionToMissionSummary(Mission mission) {
        MissionSummary missionSummary = null;
        if (mission instanceof final StandardMission standardMission) {
            StandardMissionSummary standardMissionSummary = new StandardMissionSummary();
            standardMissionSummary.setSuccessRate(standardMission.getSuccessRate());
            missionSummary = standardMissionSummary;
            missionSummary.setType(MissionTypeEnum.STANDARD.name());
        } else if (mission instanceof final CustomMission customMission) {
            CustomMissionSummary customMissionSummary = new CustomMissionSummary();
            customMissionSummary.setSubOrder(customMission.getSubOrder());
            missionSummary = customMissionSummary;
            missionSummary.setType(MissionTypeEnum.CUSTOM.name());
        }
        missionSummary.setId(mission.getId());
        missionSummary.setOrder(mission.getOrder());
        missionSummary.setIsOptional(mission.getIsOptional());
        missionSummary.setIsVisible(mission.getIsVisible());
        missionSummary.setTitle(mission.getTitle());
        missionSummary.setDescription(mission.getDescription());
        return missionSummary;
    }

    public List<MissionSummary> missionsToMissionSummaries(List<StandardMission> standardMissions) {
        return standardMissions.stream().map(this::missionToMissionSummary).toList();
    }

    public ClueSummary clueToClueSummary(Clue clue) {
        final ClueSummary clueSummary = new ClueSummary();
        clueSummary.setId(clue.getId());
        clueSummary.setContent(clue.getContent());
        clueSummary.setOrder(clue.getOrder());
        return clueSummary;
    }

    public List<ClueSummary> cluesToClueSummaries(List<Clue> clues) {
        List<ClueSummary> clueSummaries = new ArrayList<>();
        if (clues != null) {
            clueSummaries = clues.stream().map(this::clueToClueSummary).toList();
        }
        return clueSummaries;
    }

    public MissionOptionSummary missionOptionToMissionOptionSummary(MissionOption missionOption) {
        final MissionOptionSummary missionOptionSummary = new MissionOptionSummary();
        missionOptionSummary.setId(missionOption.getId());
        missionOptionSummary.setDescription(missionOption.getDescription());
        missionOptionSummary.setConditions(new ArrayList<>());
        for (MissionConditionEnum missionConditionEnum : missionOption.getConditions()) {
            missionOptionSummary.getConditions().add(missionConditionEnum.name());
        }
        return missionOptionSummary;
    }

    public PlayerSummary playerToPlayerSummary(Player player) {
        final PlayerSummary playerSummary = new PlayerSummary();
        playerSummary.setAddress(player.getAddress());
        playerSummary.setMail(player.getMail());
        playerSummary.setName(player.getName());
        playerSummary.setFirstName(player.getFirstName());
        playerSummary.setId(player.getId());
        playerSummary.setComment(player.getComment());
        playerSummary.setPhone(player.getPhone());
        return playerSummary;
    }

    public List<PlayerSummary> playersToPlayerSummaries(List<Player> players) {
        return players.stream().map(this::playerToPlayerSummary).toList();
    }

    public UniverseSummary universeToUniverseSummary(Universe universe) {
        UniverseSummary universeSummary = null;
        if (universe != null) {
            universeSummary = new UniverseSummary();
            universeSummary.setId(universe.getId());
            universeSummary.setTitle(universe.getTitle());
        }
        return universeSummary;
    }

    public SidekickSummary sidekickToSidekickSummary(Sidekick sidekick) {
        final SidekickSummary sidekickSummary = new SidekickSummary();
        sidekickSummary.setAddress(sidekick.getAddress());
        sidekickSummary.setName(sidekick.getName());
        sidekickSummary.setMail(sidekick.getMail());
        sidekickSummary.setId(sidekick.getId());
        sidekickSummary.setComment(sidekick.getComment());
        sidekickSummary.setPhone(sidekick.getPhone());
        sidekickSummary.setFirstName(sidekick.getFirstName());

        return sidekickSummary;
    }

    public EscapeSummary escapeToEscapeSummary(Escape escape) {
        final EscapeSummary escapeSummary = new EscapeSummary();
        escapeSummary.setId(escape.getId());
        escapeSummary.setDifficulty(escape.getDifficulty());
        escapeSummary.setTitle(escape.getTitle());
        escapeSummary.setSuccessRate(escape.getSuccessRate());
        return escapeSummary;
    }

    public List<EscapeSummary> escapesToEscapeSummaries(List<Escape> escapes) {
        return escapes.stream().map(this::escapeToEscapeSummary).toList();
    }

    public MissionPlayerMappingSummary missionPlayerMappingToMissionPlayerMappingSummary(MissionPlayerMapping missionPlayerMapping) {
        final MissionPlayerMappingSummary missionPlayerMappingSummary = new MissionPlayerMappingSummary();
        missionPlayerMappingSummary.setId(missionPlayerMapping.getId());
        missionPlayerMappingSummary.setPlayerId(missionPlayerMapping.getPlayer().getId());
        missionPlayerMappingSummary.setStatus(missionPlayerMapping.getStatus().name());
        missionPlayerMappingSummary.setEndDate(missionPlayerMapping.getEndDate());
        missionPlayerMappingSummary.setStartDate(missionPlayerMapping.getStartDate());
        return missionPlayerMappingSummary;
    }

    public List<MissionPlayerMappingSummary> missionPlayerMappingsToMissionPlayerMappingSummaries(List<MissionPlayerMapping> missionPlayerMappings) {
        final List<MissionPlayerMappingSummary> missionPlayerMappingSummaries = new ArrayList<>();
        for (MissionPlayerMapping missionPlayerMapping : missionPlayerMappings) {
            missionPlayerMappingSummaries.add(missionPlayerMappingToMissionPlayerMappingSummary(missionPlayerMapping));
        }
        return missionPlayerMappingSummaries;
    }

    public PlayerMissionMappingSummary missionPlayerMappingToPlayerMissionMappingSummary(MissionPlayerMapping missionPlayerMapping) {
        final PlayerMissionMappingSummary playerMissionMappingSummary = new PlayerMissionMappingSummary();
        playerMissionMappingSummary.setMissionId(missionPlayerMapping.getPlayer().getId());
        playerMissionMappingSummary.setStatus(missionPlayerMapping.getStatus().name());
        playerMissionMappingSummary.setEndDate(missionPlayerMapping.getEndDate());
        playerMissionMappingSummary.setStartDate(missionPlayerMapping.getStartDate());
        playerMissionMappingSummary.setId(missionPlayerMapping.getId());
        return playerMissionMappingSummary;
    }
}
