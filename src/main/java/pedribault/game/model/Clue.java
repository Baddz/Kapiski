package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Clues")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Clue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "content")
    private String content;
    @Column(name = "`order`")
    private Integer order;
    // when it is linked to a missionOption
    @Column(name = "sub_order")
    private Integer subOrder;

    // A clue is either linked to a mission, or a specific option of a mission
    // ie when 2 possibilities 1. package at the door 2. file to steal from colleague
    // There may be different clues for the 2 possibilities
    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne
    @JoinColumn(name = "mission_option_id")
    private MissionOption missionOption;

}
