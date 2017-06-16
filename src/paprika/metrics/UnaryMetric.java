package paprika.metrics;

import paprika.entities.Entity;

public abstract class UnaryMetric<E> extends Metric {
    protected Entity entity;

    public Entity getEntity() {
        return entity;
    }

    protected void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String toString() {
        return ((((this.entity) + " ") + (this.name)) + " : ") + (this.value);
    }

    protected void updateEntity() {
        entity.addMetric(this);
    }
}

