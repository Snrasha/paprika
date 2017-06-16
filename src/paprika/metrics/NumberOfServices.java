package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaApp;

public class NumberOfServices extends UnaryMetric<Integer> {
    private NumberOfServices(PaprikaApp paprikaApp, int value) {
        this.value = value;
        this.entity = paprikaApp;
        this.name = "number_of_services";
    }

    public static NumberOfServices createNumberOfServices(PaprikaApp paprikaApp, int value) {
        NumberOfServices numberOfServices = new NumberOfServices(paprikaApp, value);
        numberOfServices.updateEntity();
        return numberOfServices;
    }
}

