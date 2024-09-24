package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.model.dto.PlayerDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Player;
import pedribault.game.model.Sidekick;
import pedribault.game.model.dto.summary.PlayerSummary;
import pedribault.game.repository.SidekickRepository;

import java.util.List;

@Component
public class PlayerMapper {

    @Autowired
    private SidekickRepository sidekickRepository;
    @Autowired
    private SidekickMapper sidekickMapper;

    public PlayerDto playerToPlayerDto(Player player) {
        final PlayerDto playerDTO = new PlayerDto();
        playerDTO.setId(player.getId());
        playerDTO.setName(player.getName());
        playerDTO.setMail(player.getMail());
        playerDTO.setAddress(player.getAddress());
        playerDTO.setFirstName(player.getFirstName());
        if (player.getSidekicks() != null) {
            playerDTO.setSidekicks(player.getSidekicks().stream().map(s -> sidekickMapper.sidekickToSidekickSummary(s)).toList());
        }
        return playerDTO;
    }

    public Player playerDtoToPlayer(PlayerDto playerDTO) {
        final Player player = new Player();
        if (playerDTO.getId() != null) {
            player.setId(playerDTO.getId());
        }
        player.setName(playerDTO.getName());
        player.setMail(playerDTO.getMail());
        player.setAddress(playerDTO.getAddress());
        if (playerDTO.getSidekicks() != null) {
            playerDTO.getSidekicks().forEach(s -> {
                Sidekick sidekick = sidekickRepository.findById(s.getId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Sidekicks table", "The id " + s.getId() + " does not exist."));
                player.addSidekick(sidekick);
            });
        }
        player.setFirstName(playerDTO.getFirstName());

        return player;
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
}
