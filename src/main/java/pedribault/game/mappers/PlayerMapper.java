package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.dto.PlayerDto;
import pedribault.game.dto.PlayerUpdate;
import pedribault.game.dto.SidekickDto;
import pedribault.game.dto.SidekickUpdate;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Player;
import pedribault.game.model.Sidekick;
import pedribault.game.repository.SidekickRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlayerMapper {

    @Autowired
    private SidekickRepository sidekickRepository;

    public PlayerDto playerToPlayerDto(Player player) {
        final PlayerDto playerDTO = new PlayerDto();
        playerDTO.setId(player.getId());
        playerDTO.setName(player.getName());
        playerDTO.setMail(player.getMail());
        playerDTO.setAddress(player.getAddress());
        playerDTO.setFirstName(player.getFirstName());
        if (player.getSidekicks() != null) {
            playerDTO.setSidekickIds(player.getSidekicks().stream().map(p -> p.getId()).toList());
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
        if (playerDTO.getSidekickIds() != null) {
            playerDTO.getSidekickIds().forEach(id -> {
                Sidekick sidekick = sidekickRepository.findById(id).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Sidekicks table", "The id " + id + " does not exist."));
                player.addSidekick(sidekick);
            });
        }
        player.setFirstName(playerDTO.getFirstName());

        return player;
    }
}
