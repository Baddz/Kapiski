package pedribault.game.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pedribault.game.mappers.EscapePlayerMappingMapper;
import pedribault.game.model.EscapePlayerMapping;
import pedribault.game.model.dto.EscapePlayerMappingDto;
import pedribault.game.repository.EscapePlayerMappingRepository;

import java.util.List;

@Slf4j
@Service
public class EscapePlayerService {

    @Autowired
    private EscapePlayerMappingRepository escapePlayerMappingRepository;
    @Autowired
    private EscapePlayerMappingMapper escapePlayerMappingMapper;

    public List<EscapePlayerMappingDto> getEscapePlayerMappings() {
        final List<EscapePlayerMapping> escapePlayerMappings = escapePlayerMappingRepository.findAll();
        return escapePlayerMappingMapper.escapePlayerMappingsToEscapePlayerMappingDtos(escapePlayerMappings);
    }


}
