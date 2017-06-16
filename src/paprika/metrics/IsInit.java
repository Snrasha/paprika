package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaMethod;

public class IsInit extends UnaryMetric<Boolean> {
    private IsInit(PaprikaMethod entity, boolean value) {
        this.value = value;
        this.entity = entity;
        this.name = "is_init";
    }

    public static IsInit createIsInit(PaprikaMethod entity, boolean value) {
        IsInit isInit = new IsInit(entity, value);
        isInit.updateEntity();
        return isInit;
    }
}

