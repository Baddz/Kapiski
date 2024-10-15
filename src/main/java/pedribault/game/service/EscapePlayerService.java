package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pedribault.game.enums.EscapeStatusEnum;
import pedribault.game.exceptions.TheGameException;
import pedribault.game.mappers.EscapePlayerMappingMapper;
import pedribault.game.model.Escape;
import pedribault.game.model.EscapePlayerMapping;
import pedribault.game.model.Player;
import pedribault.game.model.dto.CreateOrUpdate.CreateOrUpdateEscapePlayerMapping;
import pedribault.game.model.dto.EscapePlayerMappingDto;
import pedribault.game.repository.EscapePlayerMappingRepository;
import pedribault.game.repository.EscapeRepository;
import pedribault.game.repository.PlayerRepository;

import java.util.List;

@Slf4j
@Service
public class EscapePlayerService {

    @Autowired
    private EscapePlayerMappingRepository escapePlayerMappingRepository;
    @Autowired
    private EscapePlayerMappingMapper escapePlayerMappingMapper;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private EscapeRepository escapeRepository;

    public List<EscapePlayerMappingDto> getEscapePlayerMappings() {
        final List<EscapePlayerMapping> escapePlayerMappings = escapePlayerMappingRepository.findAll();
        return escapePlayerMappingMapper.escapePlayerMappingsToEscapePlayerMappingDtos(escapePlayerMappings);
    }

    public EscapePlayerMappingDto getEscapePlayerMappingById(Integer id) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "id must be provided", "id is null");
        }
        final EscapePlayerMapping escapePlayerMapping = escapePlayerMappingRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "EscapePlayer not found", "The id " + id + " does not exist in the Escape_J_Player database"));
        final EscapePlayerMappingDto escapePlayerMappingDto = escapePlayerMappingMapper.escapePlayerMappingToEscapePlayerMappingDto(escapePlayerMapping);

        return escapePlayerMappingDto;
    }

    public List<EscapePlayerMappingDto> getEscapePlayerMappingsByPlayerId(Integer playerId) {
        if (playerId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Player id must be provided", "Player id is null");
        }

        final List<EscapePlayerMapping> escapePlayerMappings = escapePlayerMappingRepository.findByPlayerId(playerId);
        return escapePlayerMappingMapper.escapePlayerMappingsToEscapePlayerMappingDtos(escapePlayerMappings);
    }

    public List<EscapePlayerMappingDto> getEscapePlayerMappingsByEscapeId(Integer escapeId) {
        if (escapeId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Escape id must be provided", "Escape id is null");
        }

        final List<EscapePlayerMapping> escapePlayerMappings = escapePlayerMappingRepository.findByEscapeId(escapeId);
        return escapePlayerMappingMapper.escapePlayerMappingsToEscapePlayerMappingDtos(escapePlayerMappings);
    }

    public EscapePlayerMappingDto getEscapePlayerMappingsByEscapeIdAndPlayerId(Integer escapeId, Integer playerId) {
        if (escapeId == null || playerId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Escape id and player id must be provided", "Escape id or player id is null");
        }

        final EscapePlayerMapping escapePlayerMapping = escapePlayerMappingRepository.findByPlayerIdAndEscapeId(playerId, escapeId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "EscapePlayerMapping not found", "Player id: " + playerId + " and Escape id: " + escapeId + " are not linked in the Escape_J_Player database"));
        return escapePlayerMappingMapper.escapePlayerMappingToEscapePlayerMappingDto(escapePlayerMapping);
    }

    public EscapePlayerMappingDto createEscapePlayerMapping(CreateOrUpdateEscapePlayerMapping createOrUpdateEscapePlayerMapping, Integer escapeId, Integer playerId) {
        if (createOrUpdateEscapePlayerMapping == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Body is null", "A body is required");
        }
        if (escapeId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Escape id is null", "Escape id is required");
        }
        if (playerId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Player id is null", "Player id is required");
        }

        final Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Player not found", "id: " + playerId + " doesn't exist in the Players database"));
        final Escape escape = escapeRepository.findById(escapeId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Escape not found", "id: " + escapeId + " doesn't exist in the Escapes database"));

        final EscapePlayerMapping escapePlayerMapping = new EscapePlayerMapping();
        escapePlayerMapping.setPlayer(player);
        escapePlayerMapping.setEscape(escape);
        try {
            escapePlayerMapping.setStatus(EscapeStatusEnum.valueOf(createOrUpdateEscapePlayerMapping.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Invalid Escape status", "The status: " + createOrUpdateEscapePlayerMapping.getStatus() + " doesn't exist");
        }
        escapePlayerMapping.setStartDate(createOrUpdateEscapePlayerMapping.getStartDate());
        escapePlayerMapping.setEndDate(createOrUpdateEscapePlayerMapping.getEndDate());

        escapePlayerMappingRepository.save(escapePlayerMapping);

        return escapePlayerMappingMapper.escapePlayerMappingToEscapePlayerMappingDto(escapePlayerMapping);
    }

    public EscapePlayerMappingDto updateEscapePlayerMappingById(Integer id, CreateOrUpdateEscapePlayerMapping createOrUpdateEscapePlayerMapping, Integer escapeId, Integer playerId) {
        if (id == null) {
            throw  new TheGameException(HttpStatus.BAD_REQUEST, "id must be provided", "id is null");
        }
        if (createOrUpdateEscapePlayerMapping == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Body is null", "A body is required");
        }
        final EscapePlayerMapping escapePlayerMapping = escapePlayerMappingRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "EscapePlayerMapping not found", "Id: " + id + " doesn't exist in the Escape_J_Player database"));

        updateEscapePlayerMapping(createOrUpdateEscapePlayerMapping, escapePlayerMapping, escapeId, playerId);

        return escapePlayerMappingMapper.escapePlayerMappingToEscapePlayerMappingDto(escapePlayerMapping);
    }

    public EscapePlayerMappingDto updateEscapePlayerMappingByEscapeIdAndPlayerId(Integer escapeId, Integer playerId, CreateOrUpdateEscapePlayerMapping createOrUpdateEscapePlayerMapping) {
        if (playerId == null) {
            throw  new TheGameException(HttpStatus.BAD_REQUEST, "Player id must be provided", "Player id is null");
        }
        if (escapeId == null) {
            throw  new TheGameException(HttpStatus.BAD_REQUEST, "Escape id must be provided", "Escape id is null");
        }
        if (createOrUpdateEscapePlayerMapping == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Body is null", "A body is required");
        }
        final EscapePlayerMapping escapePlayerMapping = escapePlayerMappingRepository.findByPlayerIdAndEscapeId(playerId, escapeId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "EscapePlayerMapping not found", "Player id: " + playerId + " and Escape id: " + escapeId + " are not linked in the Escape_J_Player database"));

        updateEscapePlayerMapping(createOrUpdateEscapePlayerMapping, escapePlayerMapping, escapeId, playerId);

        return escapePlayerMappingMapper.escapePlayerMappingToEscapePlayerMappingDto(escapePlayerMapping);
    }

    private void updateEscapePlayerMapping(final CreateOrUpdateEscapePlayerMapping createOrUpdateEscapePlayerMapping, final EscapePlayerMapping escapePlayerMapping, Integer escapeId, Integer playerId) {
        boolean updated = false;
        if (createOrUpdateEscapePlayerMapping.getStatus() != null
                && !createOrUpdateEscapePlayerMapping.getStatus().equals(escapePlayerMapping.getStatus().name())) {
            try {
                escapePlayerMapping.setStatus(EscapeStatusEnum.valueOf(createOrUpdateEscapePlayerMapping.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new TheGameException(HttpStatus.BAD_REQUEST, "Invalid Escape status", "The status: " + createOrUpdateEscapePlayerMapping.getStatus() + "doesn't exist");
            }
            updated = true;
        }
        if (playerId != null && !playerId.equals(escapePlayerMapping.getPlayer().getId())) {
            final Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Player not found", "id: " + playerId + " doesn't exist in the Players database"));
            escapePlayerMapping.setPlayer(player);
            updated = true;
        }
        if (escapeId != null && !escapeId.equals(escapePlayerMapping.getEscape().getId())) {
            final Escape escape = escapeRepository.findById(escapeId)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Escape not found", "id: " + escapeId + " doesn't exist in the Escapes database"));
            updated = true;
        }
        if (createOrUpdateEscapePlayerMapping.getEndDate() != null
                && !createOrUpdateEscapePlayerMapping.getEndDate().equals(escapePlayerMapping.getEndDate())) {
            escapePlayerMapping.setEndDate(createOrUpdateEscapePlayerMapping.getEndDate());
            updated = true;
        }
        if (createOrUpdateEscapePlayerMapping.getStartDate() != null
                && !escapePlayerMapping.getStartDate().equals(createOrUpdateEscapePlayerMapping.getStartDate())) {
            escapePlayerMapping.setStartDate(createOrUpdateEscapePlayerMapping.getStartDate());
            updated = true;
        }
        if (updated) {
            escapePlayerMappingRepository.save(escapePlayerMapping);
        }
    }

}
