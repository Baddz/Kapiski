package pedribault.game.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlayerSummary {
    private Integer id;
    private String name;
    private String firstName;
    private String mail;
    private String address;
    private String phone;
    private String comment;
}
