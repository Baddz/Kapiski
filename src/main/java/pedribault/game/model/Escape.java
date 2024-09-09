package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Entity
@Getter
@Setter
@Data
@Table(name = "Escapes")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Escape {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "success_rate")
    private Double successRate;
    @Column(name = "difficulty")
    private Integer difficulty;

    @ManyToOne
    @JoinColumn(name = "id_universe")
    private Universe universe;

    @OneToMany(mappedBy = "escape", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mission> missions;

    @OneToMany(mappedBy = "escape", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EscapePlayer> escapePlayers;
}
