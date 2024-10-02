package pedribault.game.mappers;

import org.springframework.stereotype.Component;
import pedribault.game.enums.MissionTypeEnum;
import pedribault.game.model.*;
import pedribault.game.model.dto.summary.*;

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
        return clues.stream().map(c -> clueToClueSummary(c)).toList();
    }

    public MissionOptionSummary missionOptionToMissionOptionSummary(MissionOption missionOption) {
        final MissionOptionSummary missionOptionSummary = new MissionOptionSummary();
        missionOptionSummary.setId(missionOption.getId());
        missionOptionSummary.setMission(missionToMissionSummary(missionOption.getMission()));
        missionOptionSummary.setDescription(missionOption.getDescription());
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
        return players.stream().map(p -> playerToPlayerSummary(p)).toList();
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
}
