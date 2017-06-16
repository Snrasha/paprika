package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaClass;

public class IsInnerClass extends UnaryMetric<Boolean> {
    private IsInnerClass(PaprikaClass entity, boolean value) {
        this.value = value;
        this.entity = entity;
        this.name = "is_inner_class";
    }

    public static IsInnerClass createIsInnerClass(PaprikaClass entity, boolean value) {
        IsInnerClass isInner = new IsInnerClass(entity, value);
        isInner.updateEntity();
        return isInner;
    }
}

