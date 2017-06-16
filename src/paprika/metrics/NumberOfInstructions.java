package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaMethod;

public class NumberOfInstructions extends UnaryMetric<Integer> {
    private NumberOfInstructions(PaprikaMethod paprikaMethod, int value) {
        this.value = value;
        this.entity = paprikaMethod;
        this.name = "number_of_instructions";
    }

    public static NumberOfInstructions createNumberOfInstructions(PaprikaMethod paprikaMethod, int value) {
        NumberOfInstructions numberOfInstructions = new NumberOfInstructions(paprikaMethod, value);
        numberOfInstructions.updateEntity();
        return numberOfInstructions;
    }
}

