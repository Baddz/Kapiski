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
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "This id was not found in the Sidekicks table", "The id " + id + " does not exist."));
        } else if (name != null && firstName != null) {
            sidekick = sidekickRepository.findByNameIgnoreCaseAndFirstNameIgnoreCase(name, firstName)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "The name and first name were not found in the Sidekicks table", "The name and first name" + name + " " + firstName + " do not exist."));
        } else {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Either id or both name and firstName must be provided", "id is null, and name or firstName is null");
        }

        return sidekickMapper.sidekickToSidekickDto(sidekick);
    }

    public SidekickDto createSidekick(SidekickDto sidekickDTO) {
        if (sidekickDTO == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Sidekick is null", "A body is required");
        } else if (sidekickDTO.getName()== null || sidekickDTO.getFirstName() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The name or firstname is null", "The name and firstname of the sidekickDTO are required");
        }

        sidekickRepository.save(sidekickMapper.sidekickDtoToSidekick(sidekickDTO));

        return sidekickDTO;
    }

    public SidekickDto updateSidekick(SidekickUpdate sidekickUpdate) {
        if (sidekickUpdate == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "The input sidekick is null", "The body is missing");
        }
        if (sidekickUpdate.getId() == null && (sidekickUpdate.getName() == null || sidekickUpdate.getFirstName() == null)) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Id is null, name and first name not provided either", "The id or both name and surname must be provided");
        }

        log.info("RETREIVING SIDEKICK");

        Sidekick existingSidekick;
        if (sidekickUpdate.getId() != null) {
            existingSidekick = sidekickRepository.findById(sidekickUpdate.getId()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Sidekick not found", "The id " + sidekickUpdate.getId() + " doesn't exist in the Sidekicks database"));
        } else {
            existingSidekick = sidekickRepository.findByNameIgnoreCaseAndFirstNameIgnoreCase(sidekickUpdate.getName(), sidekickUpdate.getFirstName()).orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Sidekick not found", "The name and first name " + sidekickUpdate.getName() + " " + sidekickUpdate.getFirstName() + " don't exist in the Sidekicks database"));
        }

        log.info("SIDEKICK RETREIVED");

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
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Sidekick id " + id + " doesn't exist", "The sidekick was not found in the Sidekicks database"));
        if (playerIds != null && !playerIds.isEmpty()) {
            final List<Integer> currentPlayerIds = sidekick.getPlayers().stream().map(Player::getId).toList();
            final List<Player> playersToAdd = new ArrayList<>();
            playerIds.forEach(playerId -> {
                if (!currentPlayerIds.contains(playerId)) {
                    final Player player = playerRepository.findById(playerId)
                            .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Player id " + playerId + " doesn't exist", "The player was not found in the Players database"));
                    playersToAdd.add(player);
                }
            });
            playersToAdd.forEach(p -> {
                sidekick.addPlayer(p);
                playerRepository.save(p);
            });
            sidekickRepository.save(sidekick);
        }

        final SidekickDto sidekickDto = sidekickMapper.sidekickToSidekickDto(sidekick);

        return sidekickDto;
    }

    public SidekickDto removePlayers(Integer id, List<Integer> playerIds) {
        final Sidekick sidekick = sidekickRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Sidekick id " + id + " doesn't exist", "The sidekick was not found in the Sidekicks database"));

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

        final SidekickDto sidekickDto = sidekickMapper.sidekickToSidekickDto(sidekick);

        return sidekickDto;
    }

    public SidekickDto updatePlayers(Integer id, List<Integer> updatedPlayerIds) {
        final Sidekick sidekick = sidekickRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Sidekick id " + id + " doesn't exist", "The sidekick was not found in the Sidekicks database"));

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
                            .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Player id " + playerId + " doesn't exist", "The player was not found in the Players database"));
                    playersToAdd.add(player);
                }
            });
            playersToAdd.forEach(p -> {
                sidekick.addPlayer(p);
                playerRepository.save(p);
            });
            sidekickRepository.save(sidekick);
        }

        final SidekickDto sidekickDto = sidekickMapper.sidekickToSidekickDto(sidekick);

        return sidekickDto;
    }

}
