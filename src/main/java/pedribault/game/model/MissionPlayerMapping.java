package pedribault.game.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pedribault.game.enums.MissionStatusEnum;

import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Getter
@Setter
@Table(name = "Player_J_Mission", uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "mission_id"}))
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MissionPlayerMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MissionStatusEnum status;

    // Success tracking
    @Column(name = "was_successful")
    private Boolean wasSuccessful;

    @Column(name = "completion_time_minutes")
    private Integer completionTimeMinutes;

    // Player rating from 0 to 10
    @Column(name = "player_rating")
    private Integer playerRating;

    @PrePersist
    protected void onCreate() {
        startDate = LocalDateTime.now();
        status = MissionStatusEnum.UNLOCKED;
    }

    @PreUpdate
    protected void onUpdate() {
        if (status == MissionStatusEnum.COMPLETED && endDate != null && startDate != null) {
            completionTimeMinutes = (int) Duration.between(startDate, endDate).toMinutes();
        }
    }

    public void complete(Boolean success, Integer rating) {
        this.endDate = LocalDateTime.now();
        this.status = MissionStatusEnum.COMPLETED;
        this.wasSuccessful = success;
        if (rating != null && rating >= 0 && rating <= 10) {
            this.playerRating = rating;
        }
    }

    public void restart() {
        this.startDate = LocalDateTime.now();
        this.endDate = null;
        this.status = MissionStatusEnum.UNLOCKED;
    }
}

