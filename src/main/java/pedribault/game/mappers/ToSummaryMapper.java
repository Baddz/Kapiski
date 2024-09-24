package pedribault.game.mappers;

import org.springframework.stereotype.Component;
import pedribault.game.enums.MissionTypeEnum;
import pedribault.game.model.*;
import pedribault.game.model.dto.summary.*;

import java.util.List;

@Component
public class ToSummaryMapper {
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

    public MissionSummary missionToMissionSummaryClue(Mission mission) {
        final MissionSummary missionSummary = new MissionSummary();
        missionSummary.setId(mission.getId());
        missionSummary.setOrder(mission.getOrder());
        missionSummary.setIsOptional(mission.getIsOptional());
        missionSummary.setIsVisible(mission.getIsVisible());
        missionSummary.setTitle(mission.getTitle());
        missionSummary.setDescription(mission.getDescription());
        if (mission instanceof final StandardMission standardMission) {
            missionSummary.setSuccessRate(standardMission.getSuccessRate());
            missionSummary.setEscape(escapeToEscapeSummaryMission(standardMission.getEscape()));
            missionSummary.setType(MissionTypeEnum.STANDARD);
        } else if (mission instanceof final CustomMission customMission) {
            missionSummary.setEscape(escapeToEscapeSummaryMission(customMission.getEscape()));
            missionSummary.setSubOrder(customMission.getSubOrder());
            missionSummary.setType(MissionTypeEnum.CUSTOM);
        }
        return missionSummary;
    }

    public EscapeSummaryMission escapeToEscapeSummaryMission(Escape escape) {
        EscapeSummaryMission escapeSummaryMission = null;
        if (escape != null) {
            escapeSummaryMission = new EscapeSummaryMission();
            escapeSummaryMission.setId(escape.getId());
            escapeSummaryMission.setTitle(escape.getTitle());
            escapeSummaryMission.setUniverse(universeToUniverseSummary(escape.getUniverse()));
        }
        return escapeSummaryMission;
    }

    public List<MissionSummary> missionsToMissionSummaryClues(List<StandardMission> standardMissions) {
        return standardMissions.stream().map(this::missionToMissionSummaryClue).toList();
    }

    public MissionSummaryEscape standardMissionToMissionSummaryEscape(StandardMission standardMission) {
        final MissionSummaryEscape missionSummaryEscape = new MissionSummaryEscape();
        final StandardMissionSummary standardMissionSummary = new StandardMissionSummary();
        standardMissionSummary.setId(standardMission.getId());
        standardMissionSummary.setOrder(standardMission.getOrder());
        standardMissionSummary.setIsOptional(standardMission.getIsOptional());
        standardMissionSummary.setIsVisible(standardMission.getIsVisible());
        standardMissionSummary.setTitle(standardMission.getTitle());
        standardMissionSummary.setSuccessRate(standardMission.getSuccessRate());
        missionSummaryEscape.setStandardMissionSummary(standardMissionSummary);
        missionSummaryEscape.setClues(cluesToClueSummaries(standardMission.getClues()));
        return missionSummaryEscape;
    }

    public List<MissionSummaryEscape> standardMissionsToMissionSummaryEscapes(List<StandardMission> standardMissions) {
        return standardMissions.stream().map(m -> standardMissionToMissionSummaryEscape(m)).toList();
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

    public PlayerSummaryEscape playerToPlayerSummaryEscape(Player player) {
        final PlayerSummaryEscape playerSummaryEscape = new PlayerSummaryEscape();
        playerSummaryEscape.setPlayerSummary(playerToPlayerSummary(player));
        return playerSummaryEscape;
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
}
