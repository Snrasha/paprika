package paprika.metrics;

import paprika.entities.Entity;

public class IsFinal extends UnaryMetric<Boolean> {
    private IsFinal(Entity entity, boolean value) {
        this.value = value;
        this.entity = entity;
        this.name = "is_final";
    }

    public static IsFinal createIsFinal(Entity entity, boolean value) {
        IsFinal isFinal = new IsFinal(entity, value);
        isFinal.updateEntity();
        return isFinal;
    }
}

