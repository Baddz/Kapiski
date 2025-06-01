package pedribault.game.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.exceptions.TheGameException;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ObjectHandler {

    /**
     * Configuration class for updateObjectList to make it more flexible
     */
    @lombok.Builder
    public static class UpdateConfig<UpdateObject, OriginalObject> {
        private List<OriginalObject> originalObjects;
        private List<UpdateObject> updateObjects;
        private Function<OriginalObject, Integer> getId;
        private Function<UpdateObject, Integer> getNewId;
        private TriConsumer<OriginalObject, UpdateObject, AtomicBoolean> updateFunction;
        private Function<UpdateObject, OriginalObject> createFunction;
        private boolean allowNewIds;
        private Predicate<OriginalObject> additionalValidation;
        private BiConsumer<OriginalObject, UpdateObject> beforeUpdate;
        private BiConsumer<OriginalObject, UpdateObject> afterUpdate;
        private Comparator<OriginalObject> orderingComparator;
    }

    /**
     * Update a list of objects with enhanced configuration options
     * @param config Configuration object containing all necessary parameters and callbacks
     * @return true if any changes were made
     */
    public <UpdateObject, OriginalObject> boolean updateObjectList(UpdateConfig<UpdateObject, OriginalObject> config) {
        AtomicBoolean updated = new AtomicBoolean(false);

        // Validate inputs
        if (config.originalObjects == null || config.updateObjects == null) {
            throw new TheGameException(
                HttpStatus.BAD_REQUEST,
                "Invalid input",
                "Original and update object lists cannot be null"
            );
        }

        // Create a map of existing objects by ID
        Map<Integer, OriginalObject> existingObjectsMap = config.originalObjects.stream()
                .filter(obj -> config.getId.apply(obj) != null)
                .collect(Collectors.toMap(config.getId, Function.identity()));

        // Set of ids in the new list
        Set<Integer> newObjectIds = config.updateObjects.stream()
                .map(config.getNewId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Validate IDs if not allowing new ones
        if (!config.allowNewIds) {
            List<Integer> missingIds = config.updateObjects.stream()
                    .map(config.getNewId)
                    .filter(Objects::nonNull)
                    .filter(id -> !existingObjectsMap.containsKey(id))
                    .toList();

            if (!missingIds.isEmpty()) {
                String objectClass = !config.originalObjects.isEmpty() ? 
                        config.originalObjects.get(0).getClass().getSimpleName() : 
                        (!config.updateObjects.isEmpty() ? config.updateObjects.get(0).getClass().getSimpleName() : "Unknown");
                throw new TheGameException(
                        HttpStatus.NOT_FOUND,
                        "Error when updating list of " + objectClass,
                        "The following IDs do not exist: " + missingIds
                );
            }
        }

        // Process each new object
        for (UpdateObject newObj : config.updateObjects) {
            Integer newId = config.getNewId.apply(newObj);
            
            if (newId == null) {
                // Create new object if no ID provided
                OriginalObject createdObj = config.createFunction.apply(newObj);
                
                // Run additional validation if provided
                if (config.additionalValidation != null && !config.additionalValidation.test(createdObj)) {
                    throw new TheGameException(
                        HttpStatus.BAD_REQUEST,
                        "Validation failed",
                        "New object failed validation"
                    );
                }
                
                config.originalObjects.add(createdObj);
                updated.set(true);
            } else {
                OriginalObject existingObj = existingObjectsMap.get(newId);
                if (existingObj != null) {
                    // Run pre-update hook if provided
                    if (config.beforeUpdate != null) {
                        config.beforeUpdate.accept(existingObj, newObj);
                    }
                    
                    // Update existing object
                    config.updateFunction.accept(existingObj, newObj, updated);
                    
                    // Run post-update hook if provided
                    if (config.afterUpdate != null) {
                        config.afterUpdate.accept(existingObj, newObj);
                    }
                } else if (config.allowNewIds) {
                    // Create new object with provided ID if allowed
                    OriginalObject createdObj = config.createFunction.apply(newObj);
                    
                    // Run additional validation if provided
                    if (config.additionalValidation != null && !config.additionalValidation.test(createdObj)) {
                        throw new TheGameException(
                            HttpStatus.BAD_REQUEST,
                            "Validation failed",
                            "New object failed validation"
                        );
                    }
                    
                    config.originalObjects.add(createdObj);
                    updated.set(true);
                }
            }
        }

        // Remove obsolete objects
        boolean removedAny = config.originalObjects.removeIf(obj -> {
            Integer id = config.getId.apply(obj);
            return id != null && !newObjectIds.contains(id);
        });

        if (removedAny) {
            updated.set(true);
        }

        // Apply ordering if comparator is provided
        if (config.orderingComparator != null && updated.get()) {
            config.originalObjects.sort(config.orderingComparator);
        }

        return updated.get();
    }

    /**
     * Legacy support for the old method signature
     */
    public <UpdateObject, OriginalObject> void updateObjectList(
            List<OriginalObject> originalObjectsOld,
            List<UpdateObject> updateObjectsNew,
            Function<OriginalObject, Integer> getId,
            Function<UpdateObject, Integer> getNewId,
            TriConsumer<OriginalObject, UpdateObject, AtomicBoolean> updateFunction,
            Function<UpdateObject, OriginalObject> createFunction,
            AtomicBoolean updated,
            boolean allowNewIds) {
        
        UpdateConfig<UpdateObject, OriginalObject> config = UpdateConfig.<UpdateObject, OriginalObject>builder()
            .originalObjects(originalObjectsOld)
            .updateObjects(updateObjectsNew)
            .getId(getId)
            .getNewId(getNewId)
            .updateFunction(updateFunction)
            .createFunction(createFunction)
            .allowNewIds(allowNewIds)
            .build();

        boolean wasUpdated = updateObjectList(config);
        if (updated != null) {
            updated.set(wasUpdated);
        }
    }
}
