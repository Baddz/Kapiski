package pedribault.game.mappers;

import org.springframework.stereotype.Component;
import pedribault.game.model.Universe;
import pedribault.game.model.dto.summary.UniverseSummary;

@Component
public class UniverseMapper {

    public UniverseSummary universeToUniverseSummary(Universe universe) {
        UniverseSummary universeSummary = null;
        if (universe != null) {
            universeSummary = new UniverseSummary();
            universeSummary.setId(universe.getId());
            universeSummary.setTitle(universe.getTitle());
        }
        return universeSummary;
    }

}
