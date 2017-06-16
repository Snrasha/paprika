package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaMethod;

public class IsOverride extends UnaryMetric<Boolean> {
    private IsOverride(PaprikaMethod entity, boolean value) {
        this.value = value;
        this.entity = entity;
        this.name = "is_override";
    }

    public static IsOverride createIsOverride(PaprikaMethod entity, boolean value) {
        IsOverride isOverride = new IsOverride(entity, value);
        isOverride.updateEntity();
        return isOverride;
    }
}

