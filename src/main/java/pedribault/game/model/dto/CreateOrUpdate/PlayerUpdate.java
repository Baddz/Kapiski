package pedribault.game.model.dto.CreateOrUpdate;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PlayerUpdate {
    private Integer id;
    private String mail;
    private String address;
    private String name;
    private String firstName;
}
