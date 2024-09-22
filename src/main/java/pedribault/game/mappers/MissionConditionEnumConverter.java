package pedribault.game.mappers;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import pedribault.game.enums.MissionConditionEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class MissionConditionEnumConverter implements AttributeConverter<List<MissionConditionEnum>, String> {

    @Override
    public String convertToDatabaseColumn(List<MissionConditionEnum> conditions) {
        return conditions != null
                ? conditions.stream().map(MissionConditionEnum::name).collect(Collectors.joining(","))
                : null;
    }

    @Override
    public List<MissionConditionEnum> convertToEntityAttribute(String conditionsString) {
        return conditionsString != null
                ? Arrays.stream(conditionsString.split(",")).map(MissionConditionEnum::valueOf).toList()
                : null;
    }
}
