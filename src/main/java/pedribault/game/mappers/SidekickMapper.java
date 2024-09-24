package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.model.dto.SidekickDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.model.Player;
import pedribault.game.model.Sidekick;
import pedribault.game.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class SidekickMapper {

    private PlayerMapper playerMapper;
    private PlayerRepository playerRepository;

    @Autowired
    public SidekickMapper(PlayerMapper playerMapper, PlayerRepository playerRepository) {
        this.playerMapper = playerMapper;
        this.playerRepository = playerRepository;
    }

    public SidekickDto sidekickToSidekickDto(Sidekick sidekick) {
        final SidekickDto sidekickDTO = new SidekickDto();
        sidekickDTO.setId(sidekick.getId());
        sidekickDTO.setName(sidekick.getName());
        sidekickDTO.setMail(sidekick.getMail());
        sidekickDTO.setAddress(sidekick.getAddress());
        if (sidekick.getPlayers() != null) {
            sidekickDTO.setPlayerIds(sidekick.getPlayers().stream().map(p -> p.getId()).toList());
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
        if (sidekickDTO.getPlayerIds() != null) {
            sidekickDTO.getPlayerIds().forEach(id -> {
                Player player = playerRepository.findById(id).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Players table", "The id " + id + " does not exist."));
                sidekick.addPlayer(player);
            });
        }
        sidekick.setFirstName(sidekick.getFirstName());

        return sidekick;
    }
}
