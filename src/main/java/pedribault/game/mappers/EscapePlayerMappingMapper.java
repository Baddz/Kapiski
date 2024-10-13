package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pedribault.game.model.EscapePlayerMapping;
import pedribault.game.model.dto.EscapePlayerMappingDto;
import pedribault.game.repository.EscapePlayerMappingRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class EscapePlayerMappingMapper {

    @Autowired
    private EscapePlayerMappingRepository escapePlayerMappingRepository;
    @Autowired
    private ToSummaryMapper toSummaryMapper;

    public EscapePlayerMappingDto escapePlayerMappingToEscapePlayerMappingDto(EscapePlayerMapping escapePlayerMapping) {
        final EscapePlayerMappingDto escapePlayerMappingDto = new EscapePlayerMappingDto();
        escapePlayerMappingDto.setId(escapePlayerMapping.getId());
        escapePlayerMappingDto.setStatus(escapePlayerMapping.getStatus().name());
        escapePlayerMappingDto.setStartDate(escapePlayerMapping.getStartDate());
        escapePlayerMappingDto.setEndDate(escapePlayerMapping.getEndDate());
        escapePlayerMappingDto.setPlayer(toSummaryMapper.playerToPlayerSummary(escapePlayerMapping.getPlayer()));
        escapePlayerMappingDto.setEscape(toSummaryMapper.escapeToEscapeSummary(escapePlayerMapping.getEscape()));
        return escapePlayerMappingDto;
    }

    public List<EscapePlayerMappingDto> escapePlayerMappingsToEscapePlayerMappingDtos(List<EscapePlayerMapping> escapePlayerMappings) {
        final List<EscapePlayerMappingDto> escapePlayerMappingDtos = new ArrayList<>();
        for (EscapePlayerMapping escapePlayerMapping : escapePlayerMappings) {
            escapePlayerMappingDtos.add(escapePlayerMappingToEscapePlayerMappingDto(escapePlayerMapping));
        }
        return escapePlayerMappingDtos;
    }
}
