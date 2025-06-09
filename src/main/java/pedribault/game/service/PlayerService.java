package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pedribault.game.model.dto.PlayerDto;
import pedribault.game.model.dto.CreateOrUpdate.PlayerUpdate;
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

    public PlayerDto getPlayerById(final Integer id, final String name, final String firstName) {

        if (id == null && (name == null || firstName == null)) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[getPlayerById] Missing parameters",
                "[getPlayerById] player_id or both name and first_name must be provided");
        }

        PlayerDto playerDTO;
        if (id != null) {
            Player player = playerRepository.findById(id)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                        "[getPlayerById] Player not found",
                        "[getPlayerById] player_id: " + id + " not found"));
            playerDTO = playerMapper.playerToPlayerDto(player);
        } else {
            Player player = playerRepository.findByNameIgnoreCaseAndFirstNameIgnoreCase(name, firstName)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                        "[getPlayerById] Player not found",
                        "[getPlayerById] player with name: " + name + " and first_name: " + firstName + " not found"));
            playerDTO = playerMapper.playerToPlayerDto(player);
        }

        return playerDTO;
    }

    public PlayerDto createPlayer(PlayerDto playerDto) {
        if (playerDto == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[createPlayer] Missing payload",
                "[createPlayer] PlayerDto must be provided");
        }
        if (playerDto.getName() == null || playerDto.getFirstName() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[createPlayer] Missing required fields",
                "[createPlayer] name and first_name must be provided");
        }

        final Player player = playerMapper.playerDtoToPlayer(playerDto);
        playerRepository.save(player);
        return playerDto;
    }

    public PlayerDto updatePlayer(PlayerUpdate playerUpdate) {
        if (playerUpdate == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[updatePlayer] Missing payload",
                "[updatePlayer] PlayerUpdate must be provided");
        }
        if (playerUpdate.getId() == null && (playerUpdate.getName() == null || playerUpdate.getFirstName() == null)) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[updatePlayer] Missing parameters",
                "[updatePlayer] player_id or both name and first_name must be provided");
        }

        log.info("[updatePlayer] Retrieving player with id: {}, name: {} {}", 
            playerUpdate.getId(), playerUpdate.getFirstName(), playerUpdate.getName());

        Player existingPlayer;
        if (playerUpdate.getId() != null) {
            existingPlayer = playerRepository.findById(playerUpdate.getId())
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                    "[updatePlayer] Player not found",
                    "[updatePlayer] player_id: " + playerUpdate.getId() + " not found"));
        } else {
            existingPlayer = playerRepository.findByNameIgnoreCaseAndFirstNameIgnoreCase(playerUpdate.getName(), playerUpdate.getFirstName())
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                    "[updatePlayer] Player not found",
                    "[updatePlayer] player with name: " + playerUpdate.getName() + " and first_name: " + playerUpdate.getFirstName() + " not found"));
        }

        log.info("[updatePlayer] Found player with id: {}", existingPlayer.getId());

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
        log.info("[updatePlayer] Updated player with id: {}", existingPlayer.getId());

        return playerMapper.playerToPlayerDto(existingPlayer);
    }

    public PlayerDto addSidekicks(Integer id, List<Integer> sidekickIds) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[addSidekicks] Missing player id",
                "[addSidekicks] player_id must be provided");
        }

        final Player player = playerRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                    "[addSidekicks] Player not found",
                    "[addSidekicks] player_id: " + id + " not found"));

        if (sidekickIds != null && !sidekickIds.isEmpty()) {
            final List<Integer> currentSidekickIds = player.getSidekicks().stream().map(Sidekick::getId).toList();
            final List<Sidekick> sidekicksToAdd = new ArrayList<>();
            
            sidekickIds.forEach(sidekickId -> {
                if (!currentSidekickIds.contains(sidekickId)) {
                    final Sidekick sidekick = sidekickRepository.findById(sidekickId)
                            .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                                "[addSidekicks] Sidekick not found",
                                "[addSidekicks] sidekick_id: " + sidekickId + " not found"));
                    sidekicksToAdd.add(sidekick);
                }
            });

            sidekicksToAdd.forEach(s -> {
                player.addSidekick(s);
                sidekickRepository.save(s);
            });
            playerRepository.save(player);
            log.info("[addSidekicks] Added {} sidekicks to player_id: {}", sidekicksToAdd.size(), id);
        }

        return playerMapper.playerToPlayerDto(player);
    }

    public PlayerDto removeSidekicks(Integer id, List<Integer> sidekickIds) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[removeSidekicks] Missing player id",
                "[removeSidekicks] player_id must be provided");
        }

        final Player player = playerRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                    "[removeSidekicks] Player not found",
                    "[removeSidekicks] player_id: " + id + " not found"));

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
            log.info("[removeSidekicks] Removed {} sidekicks from player_id: {}", sidekicksToRemove.size(), id);
        }

        return playerMapper.playerToPlayerDto(player);
    }

    public PlayerDto updateSidekicks(Integer id, List<Integer> updatedSidekickIds) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST,
                "[updateSidekicks] Missing player id",
                "[updateSidekicks] player_id must be provided");
        }

        final Player player = playerRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                    "[updateSidekicks] Player not found",
                    "[updateSidekicks] player_id: " + id + " not found"));

        if (updatedSidekickIds != null && !updatedSidekickIds.isEmpty()) {
            // removing sidekicks
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
            log.info("[updateSidekicks] Removed {} sidekicks from player_id: {}", sidekicksToRemove.size(), id);

            // adding missing sidekicks
            final List<Sidekick> sidekicksToAdd = new ArrayList<>();
            updatedSidekickIds.forEach(sidekickId -> {
                if (!currentSidekicksIds.contains(sidekickId)) {
                    final Sidekick sidekick = sidekickRepository.findById(sidekickId)
                            .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND,
                                "[updateSidekicks] Sidekick not found",
                                "[updateSidekicks] sidekick_id: " + sidekickId + " not found"));
                    sidekicksToAdd.add(sidekick);
                }
            });
            sidekicksToAdd.forEach(s -> {
                player.addSidekick(s);
                sidekickRepository.save(s);
            });
            playerRepository.save(player);
            log.info("[updateSidekicks] Added {} sidekicks to player_id: {}", sidekicksToAdd.size(), id);
        }

        return playerMapper.playerToPlayerDto(player);
    }
}
