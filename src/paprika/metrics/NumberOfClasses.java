package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaApp;

public class NumberOfClasses extends UnaryMetric<Integer> {
    private NumberOfClasses(PaprikaApp paprikaApp, int value) {
        this.value = value;
        this.entity = paprikaApp;
        this.name = "number_of_classes";
    }

    public static NumberOfClasses createNumberOfClasses(PaprikaApp paprikaApp, int value) {
        NumberOfClasses numberOfClasses = new NumberOfClasses(paprikaApp, value);
        numberOfClasses.updateEntity();
        return numberOfClasses;
    }
}

