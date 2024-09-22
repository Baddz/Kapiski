package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerSummary {
    private Integer id;
    private String mail;
    private String address;
    private String name;
    private String firstName;
    private String phone;
    private String comment;
}
