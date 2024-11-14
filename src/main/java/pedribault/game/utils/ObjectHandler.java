package pedribault.game.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pedribault.game.exceptions.TheGameException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ObjectHandler {

    /**
     * Update a list of object attributes, adding the missing, updating the existing ones and removing the others
     * @param updateObjectsNew the attribute that is a list, to update
     * @param originalObjectDtosOld the list given that should be the final one
     * @param getId function to get the id of the object
     * @param updateFunction the function to update an objectDto
     * @param createFunction the function to create an objectDto
     */
    public <UpdateObject, OriginalObject> void updateObjectList(List<OriginalObject> originalObjectDtosOld,
                                                                List<UpdateObject> updateObjectsNew,
                                                                Function<OriginalObject, Integer> getId,
                                                                Function<UpdateObject, Integer> getNewId,
                                                                TriConsumer<OriginalObject, UpdateObject, AtomicBoolean> updateFunction,
                                                                Function<UpdateObject, OriginalObject> createFunction,
                                                                AtomicBoolean updated) {

        if (updated == null) {
            updated.set(false);
        }
        // Create a map of existing objects by ID
        Map<Integer, OriginalObject> existingObjectsMap = originalObjectDtosOld.stream()
                .filter(obj -> getId.apply(obj) != null)
                .collect(Collectors.toMap(getId, Function.identity()));

        // set of ids of the new list to compare
        Set<Integer> newObjectIds = updateObjectsNew.stream()
                .map(getNewId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // collect missing ids to throw potential error
        List<Integer> missingIds = updateObjectsNew.stream()
                .map(getNewId)
                .filter(Objects::nonNull)
                .filter(id -> !existingObjectsMap.containsKey(id))
                .collect(Collectors.toList());

        // If there are missing IDs, throw an error with the list of missing IDs
        if (!missingIds.isEmpty()) {
            final String objectClass = !originalObjectDtosOld.isEmpty() ? originalObjectDtosOld.get(0).getClass().toString() : (!updateObjectsNew.isEmpty() ? updateObjectsNew.get(0).getClass().toString() : "Unknown Class");
            throw new TheGameException(
                    HttpStatus.NOT_FOUND,
                    "Error when updating list of attributes",
                    "Attribute list of: " + objectClass + " doesn't contain the following list of IDs: " + missingIds
            );
        }


        for (UpdateObject newObj : updateObjectsNew) {
            Integer newId = getNewId.apply(newObj);
            // If no id, create object and add
            if (newId == null) {
                OriginalObject createdObj = createFunction.apply(newObj);
                originalObjectDtosOld.add(createdObj);
                updated.set(true);
            } else {
                OriginalObject existingObj = existingObjectsMap.get(newId);
                // if object's id in the old list, we update
                if (existingObj != null) {
                    updateFunction.accept(existingObj, newObj, updated);
                }
            }
        }
        // remove obsolete objects
        boolean removedAny = originalObjectDtosOld.removeIf(obj -> {
            Integer id = getId.apply(obj);
            return id != null && !newObjectIds.contains(id);
        });

        if (removedAny) {
            updated.set(true);
        }
    }
}
