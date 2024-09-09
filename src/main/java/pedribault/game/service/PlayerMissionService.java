package pedribault.game.service;

import org.springframework.http.HttpStatus;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.EscapePlayer;
import pedribault.game.model.PlayerMission;
import pedribault.game.repository.PlayerMissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerMissionService {

    @Autowired
    private PlayerMissionRepository playerMissionRepository;

    public PlayerMission findPlayerMissionByPlayerIdAndMissionId(Integer playerId, Integer missionId) {
        return playerMissionRepository.findByPlayerIdAndMissionId(playerId, missionId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "PlayerMission not found",
                        "PlayerMission with playerId " + playerId + " and missionId " + missionId + " not found."));
    }
}
