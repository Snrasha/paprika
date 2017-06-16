package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaClass;

public class NumberOfAttributes extends UnaryMetric<Integer> {
    private NumberOfAttributes(PaprikaClass paprikaClass, int value) {
        this.value = value;
        this.entity = paprikaClass;
        this.name = "number_of_attributes";
    }

    public static NumberOfAttributes createNumberOfAttributes(PaprikaClass paprikaClass, int value) {
        NumberOfAttributes numberOfAttributes = new NumberOfAttributes(paprikaClass, value);
        numberOfAttributes.updateEntity();
        return numberOfAttributes;
    }
}

