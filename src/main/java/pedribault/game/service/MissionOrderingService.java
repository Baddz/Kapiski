package pedribault.game.service;

import org.springframework.stereotype.Service;
import pedribault.game.model.CustomMission;
import pedribault.game.model.Mission;
import pedribault.game.model.StandardMission;
import pedribault.game.repository.MissionRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MissionOrderingService {
    private final MissionRepository missionRepository;

    public MissionOrderingService(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    public boolean isValidMissionOrder(Mission mission) {
        if (mission instanceof StandardMission) {
            return validateStandardMissionOrder((StandardMission) mission);
        } else if (mission instanceof CustomMission) {
            return validateCustomMissionOrder((CustomMission) mission);
        }
        return false;
    }

    private boolean validateStandardMissionOrder(StandardMission mission) {
        // Check if any other standard mission has the same order
        return missionRepository.findAll().stream()
                .filter(m -> m instanceof StandardMission)
                .filter(m -> m.getId() != mission.getId())
                .noneMatch(m -> m.getOrder().equals(mission.getOrder()));
    }

    private boolean validateCustomMissionOrder(CustomMission mission) {
        // For custom missions, we need to check both order and subOrder
        return missionRepository.findAll().stream()
                .filter(m -> m instanceof CustomMission)
                .filter(m -> m.getId() != mission.getId())
                .noneMatch(m -> {
                    CustomMission other = (CustomMission) m;
                    return other.getOrder().equals(mission.getOrder()) &&
                           other.getSubOrder().equals(((CustomMission) mission).getSubOrder());
                });
    }

    public List<Mission> getOrderedMissions(List<Mission> missions) {
        // Group missions by order
        Map<Integer, List<Mission>> missionsByOrder = missions.stream()
                .collect(Collectors.groupingBy(Mission::getOrder));

        return missionsByOrder.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> {
                    List<Mission> missionsWithSameOrder = entry.getValue();
                    
                    // Sort standard missions first, then custom missions by subOrder
                    return missionsWithSameOrder.stream()
                            .sorted((m1, m2) -> {
                                if (m1 instanceof StandardMission && m2 instanceof CustomMission) {
                                    return -1;
                                } else if (m1 instanceof CustomMission && m2 instanceof StandardMission) {
                                    return 1;
                                } else if (m1 instanceof CustomMission && m2 instanceof CustomMission) {
                                    return ((CustomMission) m1).getSubOrder()
                                            .compareTo(((CustomMission) m2).getSubOrder());
                                }
                                return 0;
                            });
                })
                .collect(Collectors.toList());
    }

    public Integer getNextAvailableSubOrder(Integer order) {
        return missionRepository.findAll().stream()
                .filter(m -> m instanceof CustomMission)
                .filter(m -> m.getOrder().equals(order))
                .map(m -> ((CustomMission) m).getSubOrder())
                .max(Comparator.naturalOrder())
                .map(maxSubOrder -> maxSubOrder + 1)
                .orElse(0);
    }
} 