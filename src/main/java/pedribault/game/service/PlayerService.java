package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pedribault.game.dto.PlayerDto;
import pedribault.game.dto.PlayerUpdate;
import pedribault.game.dto.SidekickDto;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.PlayerMapper;
import pedribault.game.model.Player;
import pedribault.game.model.Sidekick;
import pedribault.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pedribault.game.repository.SidekickRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private SidekickRepository sidekickRepository;
    @Autowired
    private PlayerMapper playerMapper;

    public List<PlayerDto> getPlayers() {
        final List<Player> players = playerRepository.findAll() == null ? new ArrayList<>() : playerRepository.findAll();
        final List<PlayerDto> playerDtos = new ArrayList<>();
        if (!players.isEmpty()) {
            playerDtos.addAll(players.stream().map(p -> playerMapper.playerToPlayerDto(p)).toList());
        }
        return playerDtos;
    }

    public PlayerDto getPlayer(final Integer id, final String name, final String firstName) {

        PlayerDto playerDTO;
        if (id != null) {
            Player player = playerRepository.findById(id)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Players table", "The id " + id + " does not exist."));
            playerDTO = playerMapper.playerToPlayerDto(player);
        } else if (name != null && firstName != null) {
            Player player = playerRepository.findByNameIgnoreCaseAndFirstNameIgnoreCase(name, firstName)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "The name and first name were not found in the Players table", "The name and first name" + name + " " + firstName + " do not exist."));
            playerDTO = playerMapper.playerToPlayerDto(player);
        } else {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Either id or both name and firstName must be provided", "id is null, and name or firstName is null");
        }

        return playerDTO;
    }

    public PlayerDto createPlayer(PlayerDto playerDto) {
        if (playerDto == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Player is null", "A body is required");
        } else if (playerDto.getName()== null || playerDto.getFirstName() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The name or firstname is null", "The name and firstname of the playerDto are required");
        }

        final Player player = playerMapper.playerDtoToPlayer(playerDto);

        playerRepository.save(player);

        return playerDto;
    }

    public PlayerDto updatePlayer(PlayerUpdate playerUpdate) {
        if (playerUpdate == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The input player is null", "The body is missing");
        }
        if (playerUpdate.getId() == null && (playerUpdate.getName() == null || playerUpdate.getFirstName() == null)) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id is null, name and first name not provided either", "The id or both name and surname must be provided");
        }

        log.info("RETREIVING PLAYER");

        Player existingPlayer;
        if (playerUpdate.getId() != null) {
            existingPlayer = playerRepository.findById(playerUpdate.getId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Player not found", "The id " + playerUpdate.getId() + " doesn't exist in the Players database"));
        } else {
            existingPlayer = playerRepository.findByNameIgnoreCaseAndFirstNameIgnoreCase(playerUpdate.getName(), playerUpdate.getFirstName()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Player not found", "The name and first name " + playerUpdate.getName() + " " + playerUpdate.getFirstName() + " don't exist in the Players database"));
        }

        log.info("PLAYER RETREIVED");

        if (playerUpdate.getMail() != null) {
            existingPlayer.setMail(playerUpdate.getMail());
        }
        if (playerUpdate.getAddress() != null) {
            existingPlayer.setAddress(playerUpdate.getAddress());
        }
        if (playerUpdate.getName() != null) {
            existingPlayer.setName(playerUpdate.getName());
        }
        if (playerUpdate.getFirstName() != null) {
            existingPlayer.setFirstName(playerUpdate.getFirstName());
        }

        playerRepository.save(existingPlayer);

        return playerMapper.playerToPlayerDto(existingPlayer);
    }

    public PlayerDto addSidekicks(Integer id, List<Integer> sideckikIds) {

        final Player player = playerRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Player id " + id + " doesn't exist", "The player was not found in the Players database"));
        if (sideckikIds != null && !sideckikIds.isEmpty()) {
            final List<Integer> currentSidekickIds = player.getSidekicks().stream().map(Sidekick::getId).toList();
            final List<Sidekick> sidekicksToAdd = new ArrayList<>();
            sideckikIds.forEach(sidekickId -> {
                if (!currentSidekickIds.contains(sidekickId)) {
                    final Sidekick sidekick = sidekickRepository.findById(sidekickId)
                            .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Sidekick id " + sidekickId + " doesn't exist", "The sidekick was not found in the Sidekicks database"));
                    sidekicksToAdd.add(sidekick);
                }
            });
            sidekicksToAdd.forEach(p -> {
                player.addSidekick(p);
                sidekickRepository.save(p);
            });
            playerRepository.save(player);
        }

        final PlayerDto playerDto = playerMapper.playerToPlayerDto(player);

        return playerDto;
    }

    public PlayerDto removeSidekicks(Integer id, List<Integer> sidekickIds) {
        final Player player = playerRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Player id " + id + " doesn't exist", "The player was not found in the Players database"));

        if (sidekickIds != null && !sidekickIds.isEmpty()) {
            final List<Sidekick> sidekicksToRemove = new ArrayList<>();
            player.getSidekicks().forEach(s -> {
                if (!sidekickIds.contains(s.getId())) {
                    sidekicksToRemove.add(s);
                }
            });
            sidekicksToRemove.forEach(s -> {
                player.removeSidekick(s);
                sidekickRepository.save(s);
            });
            playerRepository.save(player);
        }

        final PlayerDto playerDto = playerMapper.playerToPlayerDto(player);

        return playerDto;
    }
//TODO
    public PlayerDto updateSidekicks(Integer id, List<Integer> updatedSidekickIds) {
        final Player player = playerRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Player id " + id + " doesn't exist", "The player was not found in the Players database"));

        if (updatedSidekickIds != null && !updatedSidekickIds.isEmpty()) {
            final List<Integer> currentSidekicksIds = player.getSidekicks().stream().map(Sidekick::getId).toList();
            final List<Sidekick> sidekicksToRemove = new ArrayList<>();
            player.getSidekicks().forEach(s -> {
                if (!updatedSidekickIds.contains(s.getId())) {
                    sidekicksToRemove.add(s);
                }
            });
            sidekicksToRemove.forEach(s -> {
                player.removeSidekick(s);
                sidekickRepository.save(s);
            });
            final List<Sidekick> sidekicksToAdd = new ArrayList<>();
            updatedSidekickIds.forEach(sidekickId -> {
                if (!currentSidekicksIds.contains(sidekickId)) {
                    final Sidekick sidekick = sidekickRepository.findById(sidekickId)
                            .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Sidekick id " + sidekickId + " doesn't exist", "The sidekick was not found in the Sidekicks database"));
                    sidekicksToAdd.add(sidekick);
                }
            });
            sidekicksToAdd.forEach(s -> {
                player.addSidekick(s);
                sidekickRepository.save(s);
            });
            playerRepository.save(player);
        }

        final PlayerDto playerDto = playerMapper.playerToPlayerDto(player);

        return playerDto;
    }
}
