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

        final EscapePlayerMapping escapePlayerMapping = escapePlayerMappingRepository.findByPlayerIdAndEscapeId(playerId, escapeId);
        return escapePlayerMappingMapper.escapePlayerMappingToEscapePlayerMappingDto(escapePlayerMapping);
    }

    public EscapePlayerMappingDto createEscapePlayerMapping(CreateOrUpdateEscapePlayerMapping createOrUpdateEscapePlayerMapping) {
        if (createOrUpdateEscapePlayerMapping == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Body is null", "A body is required");
        }
        if (createOrUpdateEscapePlayerMapping.getEscapeId() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Escape id is null", "Escape id is required");
        }
        if (createOrUpdateEscapePlayerMapping.getPlayerId() == null) {
            throw new TheGameException(HttpStatus.BAD_REQUEST, "Player id is null", "Player id is required");
        }

        final Player player = playerRepository.findById(createOrUpdateEscapePlayerMapping.getPlayerId())
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Player not found", "id: " + createOrUpdateEscapePlayerMapping.getPlayerId() + " doesn't exist in the Players database"));
        final Escape escape = escapeRepository.findById(createOrUpdateEscapePlayerMapping.getEscapeId())
                .orElseThrow(() -> new TheGameException(HttpStatus.NOT_FOUND, "Escape not found", "id: " + createOrUpdateEscapePlayerMapping.getEscapeId() + " doesn't exist in the Escapes database"));

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



}
