package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaClass;

public class NumberOfImplementedInterfaces extends UnaryMetric<Integer> {
    private NumberOfImplementedInterfaces(PaprikaClass paprikaClass, int value) {
        this.value = value;
        this.entity = paprikaClass;
        this.name = "number_of_implemented_interfaces";
    }

    public static NumberOfImplementedInterfaces createNumberOfImplementedInterfaces(PaprikaClass paprikaClass, int value) {
        NumberOfImplementedInterfaces numberOfImplementedInterfaces = new NumberOfImplementedInterfaces(paprikaClass, value);
        numberOfImplementedInterfaces.updateEntity();
        return numberOfImplementedInterfaces;
    }
}

