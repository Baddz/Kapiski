package pedribault.game.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pedribault.game.model.Universe;
import pedribault.game.model.dto.UniverseDto;
import pedribault.game.model.dto.summary.UniverseSummary;

@Component
public class UniverseMapper {

    @Autowired
    private ToSummaryMapper toSummaryMapper;

    public UniverseDto universeToUniversDTO (Universe universe){
        final UniverseDto universeDto = new UniverseDto();
        universeDto.setId(universe.getId());
        universeDto.setTitle(universe.getTitle());
        if (universe.getEscapes() != null) {
            universeDto.setEscapes(toSummaryMapper.escapesToEscapeSummaries(universe.getEscapes()));
        }
        return universeDto;
    }


}
