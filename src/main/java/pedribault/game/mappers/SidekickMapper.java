package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.model.dto.SidekickDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Player;
import pedribault.game.model.Sidekick;
import pedribault.game.model.dto.summary.PlayerSummary;
import pedribault.game.model.dto.summary.SidekickSummary;
import pedribault.game.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class SidekickMapper {

    private PlayerRepository playerRepository;

    @Autowired
    public SidekickMapper(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public SidekickDto sidekickToSidekickDto(Sidekick sidekick) {
        final SidekickDto sidekickDTO = new SidekickDto();
        sidekickDTO.setId(sidekick.getId());
        sidekickDTO.setName(sidekick.getName());
        sidekickDTO.setMail(sidekick.getMail());
        sidekickDTO.setAddress(sidekick.getAddress());
        if (sidekick.getPlayers() != null) {
            final List<PlayerSummary> playerSummaries = sidekick.getPlayers().stream().map(p -> {
                final PlayerSummary playerSummary = new PlayerSummary();
                playerSummary.setId(p.getId());
                playerSummary.setMail(p.getMail());
                playerSummary.setName(p.getName());
                playerSummary.setAddress(p.getAddress());
                playerSummary.setComment(p.getComment());
                playerSummary.setFirstName(p.getFirstName());
                playerSummary.setPhone(p.getPhone());
                return playerSummary;
            }).toList();

            sidekickDTO.setPlayers(playerSummaries);
        }
        sidekickDTO.setFirstName(sidekick.getFirstName());

        return sidekickDTO;
    }

    public List<SidekickDto> sidekicksToSidekickDtos(List<Sidekick> sidekicks) {
        final List<SidekickDto> sidekickDtos = new ArrayList<>();
        if (sidekicks != null) {
            sidekickDtos.addAll(sidekicks.stream().map(s -> sidekickToSidekickDto(s)).toList());
        }
        return sidekickDtos;
    }

    public Sidekick sidekickDtoToSidekick(SidekickDto sidekickDTO) {
        final Sidekick sidekick = new Sidekick();
        if (sidekickDTO.getId() != null) {
            sidekick.setId(sidekickDTO.getId());
        }
        sidekick.setName(sidekickDTO.getName());
        sidekick.setMail(sidekickDTO.getMail());
        sidekick.setAddress(sidekickDTO.getAddress());
        if (sidekickDTO.getPlayers() != null) {
            sidekickDTO.getPlayers().forEach(p -> {
                Player player = playerRepository.findById(p.getId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Players table", "The id " + p.getId() + " does not exist."));
                sidekick.addPlayer(player);
            });
        }
        sidekick.setFirstName(sidekick.getFirstName());

        return sidekick;
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
