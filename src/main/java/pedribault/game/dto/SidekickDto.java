package pedribault.game.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
public class SidekickDto {
    private Integer id;
    private String name;
    private String firstName;
    private String mail;
    private String address;
    private String phone;
    private String comment;
    private List<PlayerSummary> players;
}
