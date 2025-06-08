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
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[getEscapePlayerMappingById] Id is null", 
                "[getEscapePlayerMappingById] escape_player_mapping_id must be provided");
        }
        final EscapePlayerMapping escapePlayerMapping = escapePlayerMappingRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[getEscapePlayerMappingById] Mapping not found", 
                    "[getEscapePlayerMappingById] escape_player_mapping_id: " + id + " doesn't exist"));
        return escapePlayerMappingMapper.escapePlayerMappingToEscapePlayerMappingDto(escapePlayerMapping);
    }

    public List<EscapePlayerMappingDto> getEscapePlayerMappingsByPlayerId(Integer playerId) {
        if (playerId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[getEscapePlayerMappingsByPlayerId] Id is null", 
                "[getEscapePlayerMappingsByPlayerId] player_id must be provided");
        }

        final List<EscapePlayerMapping> escapePlayerMappings = escapePlayerMappingRepository.findByPlayerId(playerId);
        return escapePlayerMappingMapper.escapePlayerMappingsToEscapePlayerMappingDtos(escapePlayerMappings);
    }

    public List<EscapePlayerMappingDto> getEscapePlayerMappingsByEscapeId(Integer escapeId) {
        if (escapeId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[getEscapePlayerMappingsByEscapeId] Id is null", 
                "[getEscapePlayerMappingsByEscapeId] escape_id must be provided");
        }

        final List<EscapePlayerMapping> escapePlayerMappings = escapePlayerMappingRepository.findByEscapeId(escapeId);
        return escapePlayerMappingMapper.escapePlayerMappingsToEscapePlayerMappingDtos(escapePlayerMappings);
    }

    public EscapePlayerMappingDto getEscapePlayerMappingsByEscapeIdAndPlayerId(Integer escapeId, Integer playerId) {
        if (escapeId == null || playerId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[getEscapePlayerMappingsByEscapeIdAndPlayerId] Missing ids", 
                "[getEscapePlayerMappingsByEscapeIdAndPlayerId] escape_id and player_id must be provided");
        }

        final EscapePlayerMapping escapePlayerMapping = escapePlayerMappingRepository.findByPlayerIdAndEscapeId(playerId, escapeId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[getEscapePlayerMappingsByEscapeIdAndPlayerId] Mapping not found", 
                    "[getEscapePlayerMappingsByEscapeIdAndPlayerId] No mapping found for player_id: " + playerId + " and escape_id: " + escapeId));
        return escapePlayerMappingMapper.escapePlayerMappingToEscapePlayerMappingDto(escapePlayerMapping);
    }

    public EscapePlayerMappingDto createEscapePlayerMapping(CreateOrUpdateEscapePlayerMapping createOrUpdateEscapePlayerMapping, Integer escapeId, Integer playerId) {
        if (createOrUpdateEscapePlayerMapping == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[createEscapePlayerMapping] Missing body", 
                "[createEscapePlayerMapping] CreateOrUpdateEscapePlayerMapping must be provided");
        }
        if (escapeId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[createEscapePlayerMapping] Missing escape_id", 
                "[createEscapePlayerMapping] escape_id must be provided");
        }
        if (playerId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[createEscapePlayerMapping] Missing player_id", 
                "[createEscapePlayerMapping] player_id must be provided");
        }

        final Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[createEscapePlayerMapping] Player not found", 
                    "[createEscapePlayerMapping] player_id: " + playerId + " doesn't exist"));
        final Escape escape = escapeRepository.findById(escapeId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[createEscapePlayerMapping] Escape not found", 
                    "[createEscapePlayerMapping] escape_id: " + escapeId + " doesn't exist"));

        final EscapePlayerMapping escapePlayerMapping = new EscapePlayerMapping();
        escapePlayerMapping.setPlayer(player);
        escapePlayerMapping.setEscape(escape);
        try {
            escapePlayerMapping.setStatus(EscapeStatusEnum.valueOf(createOrUpdateEscapePlayerMapping.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[createEscapePlayerMapping] Invalid status", 
                "[createEscapePlayerMapping] status: " + createOrUpdateEscapePlayerMapping.getStatus() + " is not a valid EscapeStatusEnum");
        }
        escapePlayerMapping.setStartDate(createOrUpdateEscapePlayerMapping.getStartDate());
        escapePlayerMapping.setEndDate(createOrUpdateEscapePlayerMapping.getEndDate());

        escapePlayerMappingRepository.save(escapePlayerMapping);
        return escapePlayerMappingMapper.escapePlayerMappingToEscapePlayerMappingDto(escapePlayerMapping);
    }

    public EscapePlayerMappingDto updateEscapePlayerMappingById(Integer id, CreateOrUpdateEscapePlayerMapping createOrUpdateEscapePlayerMapping, Integer escapeId, Integer playerId) {
        if (id == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateEscapePlayerMappingById] Missing id", 
                "[updateEscapePlayerMappingById] escape_player_mapping_id must be provided");
        }
        if (createOrUpdateEscapePlayerMapping == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateEscapePlayerMappingById] Missing body", 
                "[updateEscapePlayerMappingById] CreateOrUpdateEscapePlayerMapping must be provided");
        }
        final EscapePlayerMapping escapePlayerMapping = escapePlayerMappingRepository.findById(id)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[updateEscapePlayerMappingById] Mapping not found", 
                    "[updateEscapePlayerMappingById] escape_player_mapping_id: " + id + " doesn't exist"));

        updateEscapePlayerMapping(createOrUpdateEscapePlayerMapping, escapePlayerMapping, escapeId, playerId);
        return escapePlayerMappingMapper.escapePlayerMappingToEscapePlayerMappingDto(escapePlayerMapping);
    }

    public EscapePlayerMappingDto updateEscapePlayerMappingByEscapeIdAndPlayerId(Integer escapeId, Integer playerId, CreateOrUpdateEscapePlayerMapping createOrUpdateEscapePlayerMapping) {
        if (playerId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateEscapePlayerMappingByEscapeIdAndPlayerId] Missing player_id", 
                "[updateEscapePlayerMappingByEscapeIdAndPlayerId] player_id must be provided");
        }
        if (escapeId == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateEscapePlayerMappingByEscapeIdAndPlayerId] Missing escape_id", 
                "[updateEscapePlayerMappingByEscapeIdAndPlayerId] escape_id must be provided");
        }
        if (createOrUpdateEscapePlayerMapping == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, 
                "[updateEscapePlayerMappingByEscapeIdAndPlayerId] Missing body", 
                "[updateEscapePlayerMappingByEscapeIdAndPlayerId] CreateOrUpdateEscapePlayerMapping must be provided");
        }
        final EscapePlayerMapping escapePlayerMapping = escapePlayerMappingRepository.findByPlayerIdAndEscapeId(playerId, escapeId)
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                    "[updateEscapePlayerMappingByEscapeIdAndPlayerId] Mapping not found", 
                    "[updateEscapePlayerMappingByEscapeIdAndPlayerId] No mapping found for player_id: " + playerId + " and escape_id: " + escapeId));

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
                throw new TheGameException(HttpStatus.BAD_REQUEST, 
                    "[updateEscapePlayerMapping] Invalid status", 
                    "[updateEscapePlayerMapping] status: " + createOrUpdateEscapePlayerMapping.getStatus() + " is not a valid EscapeStatusEnum");
            }
            updated = true;
        }
        if (playerId != null && !playerId.equals(escapePlayerMapping.getPlayer().getId())) {
            final Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                        "[updateEscapePlayerMapping] Player not found", 
                        "[updateEscapePlayerMapping] player_id: " + playerId + " doesn't exist"));
            escapePlayerMapping.setPlayer(player);
            updated = true;
        }
        if (escapeId != null && !escapeId.equals(escapePlayerMapping.getEscape().getId())) {
            final Escape escape = escapeRepository.findById(escapeId)
                    .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, 
                        "[updateEscapePlayerMapping] Escape not found", 
                        "[updateEscapePlayerMapping] escape_id: " + escapeId + " doesn't exist"));
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
