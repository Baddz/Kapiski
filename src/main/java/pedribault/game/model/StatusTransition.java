package pedribault.game.model;

import pedribault.game.enums.EscapeStatusEnum;
import pedribault.game.enums.MissionStatusEnum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StatusTransition {
    private static final Map<EscapeStatusEnum, Set<EscapeStatusEnum>> validEscapeTransitions = new HashMap<>();
    private static final Map<MissionStatusEnum, Set<MissionStatusEnum>> validMissionTransitions = new HashMap<>();

    static {
        // Initialize Escape status transitions
        validEscapeTransitions.put(EscapeStatusEnum.NEW, Set.of(EscapeStatusEnum.STARTING));
        validEscapeTransitions.put(EscapeStatusEnum.STARTING, Set.of(EscapeStatusEnum.PLAYING, EscapeStatusEnum.CANCELED));
        validEscapeTransitions.put(EscapeStatusEnum.PLAYING, Set.of(EscapeStatusEnum.PAUSED, EscapeStatusEnum.COMPLETED, EscapeStatusEnum.CANCELED));
        validEscapeTransitions.put(EscapeStatusEnum.PAUSED, Set.of(EscapeStatusEnum.PLAYING, EscapeStatusEnum.CANCELED));
        validEscapeTransitions.put(EscapeStatusEnum.COMPLETED, new HashSet<>());  // Terminal state
        validEscapeTransitions.put(EscapeStatusEnum.CANCELED, new HashSet<>());   // Terminal state

        // Initialize Mission status transitions
        validMissionTransitions.put(MissionStatusEnum.LOCKED, Set.of(MissionStatusEnum.UNLOCKED));
        validMissionTransitions.put(MissionStatusEnum.UNLOCKED, Set.of(MissionStatusEnum.COMPLETED));
        validMissionTransitions.put(MissionStatusEnum.COMPLETED, new HashSet<>()); // Terminal state
    }

    public static boolean isValidEscapeTransition(EscapeStatusEnum currentStatus, EscapeStatusEnum newStatus) {
        Set<EscapeStatusEnum> validNextStates = validEscapeTransitions.get(currentStatus);
        return validNextStates != null && validNextStates.contains(newStatus);
    }

    public static boolean isValidMissionTransition(MissionStatusEnum currentStatus, MissionStatusEnum newStatus) {
        Set<MissionStatusEnum> validNextStates = validMissionTransitions.get(currentStatus);
        return validNextStates != null && validNextStates.contains(newStatus);
    }
} 