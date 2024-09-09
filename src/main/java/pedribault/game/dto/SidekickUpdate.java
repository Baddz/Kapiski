package pedribault.game.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
