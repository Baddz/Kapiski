package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import pedribault.game.model.dto.SidekickDto;
import pedribault.game.model.dto.CreateOrUpdate.SidekickUpdate;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.SidekickMapper;
import pedribault.game.model.Player;
import pedribault.game.model.Sidekick;
import pedribault.game.repository.PlayerRepository;
import pedribault.game.repository.SidekickRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SidekickService {

    @Autowired
    private SidekickRepository sidekickRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private SidekickMapper sidekickMapper;

    public List<SidekickDto> getSidekicks() {
        final List<Sidekick> sidekicks = sidekickRepository.findAll() == null ? new ArrayList<>() : sidekickRepository.findAll();
        return sidekickMapper.sidekicksToSidekickDtos(sidekicks);
    }

    public SidekickDto getSidekick(final Integer id, final String name, final String firstName) {
        Sidekick sidekick;
        if (id != null) {
            sidekick = sidekickRepository.findById(id)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "[getSidekick] Sidekick not found", "sidekick_id " + id + " does not exist"));
        } else if (name != null && firstName != null) {
            sidekick = sidekickRepository.findByNameIgnoreCaseAndFirstNameIgnoreCase(name, firstName)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "[getSidekick] Sidekick not found", "Sidekick with name and first_name " + name + " " + firstName + " does not exist"));
        } else {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "[getSidekick] missing parameters", "either sidekick_id or both name and first_name must be provided");
        }

        return sidekickMapper.sidekickToSidekickDto(sidekick);
    }

    public SidekickDto createSidekick(SidekickDto sidekickDTO) {
        if (sidekickDTO == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "[createSidekick] missing payload", "SidekickDto must be provided");
        } else if (sidekickDTO.getName() == null || sidekickDTO.getFirstName() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "[createSidekick] missing required fields", "name and first_name of the sidekickDto are required");
        }

        sidekickRepository.save(sidekickMapper.sidekickDtoToSidekick(sidekickDTO));
        return sidekickDTO;
    }

    public SidekickDto updateSidekick(SidekickUpdate sidekickUpdate) {
        if (sidekickUpdate == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "[updateSidekick] missing payload", "sidekickUpdate must be provided");
        }
        if (sidekickUpdate.getId() == null && (sidekickUpdate.getName() == null || sidekickUpdate.getFirstName() == null)) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "[updateSidekick] missing required fields", "either sidekick_id or both name and first_name must be provided");
        }

        Sidekick existingSidekick;
        if (sidekickUpdate.getId() != null) {
            existingSidekick = sidekickRepository.findById(sidekickUpdate.getId())
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "[updateSidekick] Sidekick not found", "sidekick_id " + sidekickUpdate.getId() + " does not exist"));
        } else {
            existingSidekick = sidekickRepository.findByNameIgnoreCaseAndFirstNameIgnoreCase(sidekickUpdate.getName(), sidekickUpdate.getFirstName())
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "[updateSidekick] Sidekick not found", "Sidekick with name and first_name " + sidekickUpdate.getName() + " " + sidekickUpdate.getFirstName() + " does not exist"));
        }

        if (sidekickUpdate.getMail() != null) {
            existingSidekick.setMail(sidekickUpdate.getMail());
        }
        if (sidekickUpdate.getAddress() != null) {
            existingSidekick.setAddress(sidekickUpdate.getAddress());
        }
        if (sidekickUpdate.getName() != null) {
            existingSidekick.setName(sidekickUpdate.getName());
        }
        if (sidekickUpdate.getFirstName() != null) {
            existingSidekick.setFirstName(sidekickUpdate.getFirstName());
        }

        sidekickRepository.save(existingSidekick);
        return sidekickMapper.sidekickToSidekickDto(existingSidekick);
    }

    public SidekickDto addPlayers(Integer id, List<Integer> playerIds) {
        final Sidekick sidekick = sidekickRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "[addPlayers] Sidekick not found", "sidekick_id " + id + " does not exist"));

        if (playerIds != null && !playerIds.isEmpty()) {
            final List<Integer> currentPlayerIds = sidekick.getPlayers().stream().map(Player::getId).toList();
            final List<Player> playersToAdd = new ArrayList<>();
            playerIds.forEach(playerId -> {
                if (!currentPlayerIds.contains(playerId)) {
                    final Player player = playerRepository.findById(playerId)
                            .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "[addPlayers] Player not found", "player_id " + playerId + " does not exist"));
                    playersToAdd.add(player);
                }
            });
            playersToAdd.forEach(p -> {
                sidekick.addPlayer(p);
                playerRepository.save(p);
            });
            sidekickRepository.save(sidekick);
        }

        return sidekickMapper.sidekickToSidekickDto(sidekick);
    }

    public SidekickDto removePlayers(Integer id, List<Integer> playerIds) {
        final Sidekick sidekick = sidekickRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "[removePlayers] Sidekick not found", "sidekick_id " + id + " does not exist"));

        if (playerIds != null && !playerIds.isEmpty()) {
            final List<Player> playersToRemove = new ArrayList<>();
            sidekick.getPlayers().forEach(p -> {
                if (!playerIds.contains(p.getId())) {
                    playersToRemove.add(p);
                }
            });
            playersToRemove.forEach(p -> {
                sidekick.removePlayer(p);
                playerRepository.save(p);
            });
            sidekickRepository.save(sidekick);
        }

        return sidekickMapper.sidekickToSidekickDto(sidekick);
    }

    public SidekickDto updatePlayers(Integer id, List<Integer> updatedPlayerIds) {
        final Sidekick sidekick = sidekickRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "[updatePlayers] Sidekick not found", "sidekick_id " + id + " does not exist"));

        if (updatedPlayerIds != null && !updatedPlayerIds.isEmpty()) {
            final List<Integer> currentPlayerIds = sidekick.getPlayers().stream().map(Player::getId).toList();
            final List<Player> playersToRemove = new ArrayList<>();
            sidekick.getPlayers().forEach(p -> {
                if (!updatedPlayerIds.contains(p.getId())) {
                    playersToRemove.add(p);
                }
            });
            playersToRemove.forEach(p -> {
                sidekick.removePlayer(p);
                playerRepository.save(p);
            });
            final List<Player> playersToAdd = new ArrayList<>();
            updatedPlayerIds.forEach(playerId -> {
                if (!currentPlayerIds.contains(playerId)) {
                    final Player player = playerRepository.findById(playerId)
                            .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "[updatePlayers] Player not found", "player_id " + playerId + " does not exist"));
                    playersToAdd.add(player);
                }
            });
            playersToAdd.forEach(p -> {
                sidekick.addPlayer(p);
                playerRepository.save(p);
            });
            sidekickRepository.save(sidekick);
        }

        return sidekickMapper.sidekickToSidekickDto(sidekick);
    }
}
