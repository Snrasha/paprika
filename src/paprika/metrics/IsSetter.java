package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaMethod;

public class IsSetter extends UnaryMetric<Boolean> {
    private IsSetter(PaprikaMethod entity, boolean value) {
        this.value = value;
        this.entity = entity;
        this.name = "is_setter";
    }

    public static IsSetter createIsSetter(PaprikaMethod entity, boolean value) {
        IsSetter isSetter = new IsSetter(entity, value);
        isSetter.updateEntity();
        return isSetter;
    }
}

