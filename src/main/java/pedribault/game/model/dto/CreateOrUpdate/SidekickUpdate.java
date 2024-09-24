package pedribault.game.model.dto.CreateOrUpdate;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class SidekickUpdate {
    private Integer id;
    private String address;
    private String mail;
    private String name;
    private String firstName;
}
