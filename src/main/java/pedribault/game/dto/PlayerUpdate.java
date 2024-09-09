package pedribault.game.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
