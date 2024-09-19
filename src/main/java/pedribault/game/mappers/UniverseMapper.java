package pedribault.game.mappers;

import org.springframework.stereotype.Component;
import pedribault.game.dto.UniverseSummary;
import pedribault.game.model.Universe;

@Component
public class UniverseMapper {

    public UniverseSummary universeToUniverseSummary(Universe universe) {
        final UniverseSummary universeSummary = new UniverseSummary();
        universeSummary.setId(universe.getId());
        universeSummary.setTitle(universe.getTitle());
        return universeSummary;
    }
}
