package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaApp;

public class NumberOfVariables extends UnaryMetric<Integer> {
    private NumberOfVariables(PaprikaApp paprikaApp, int value) {
        this.value = value;
        this.entity = paprikaApp;
        this.name = "number_of_variables";
    }

    public static NumberOfVariables createNumberOfVariables(PaprikaApp paprikaApp, int value) {
        NumberOfVariables numberOfVariables = new NumberOfVariables(paprikaApp, value);
        numberOfVariables.updateEntity();
        return numberOfVariables;
    }
}

