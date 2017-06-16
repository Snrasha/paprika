package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaApp;

public class NumberOfContentProviders extends UnaryMetric<Integer> {
    private NumberOfContentProviders(PaprikaApp paprikaApp, int value) {
        this.value = value;
        this.entity = paprikaApp;
        this.name = "number_of_content_providers";
    }

    public static NumberOfContentProviders createNumberOfContentProviders(PaprikaApp paprikaApp, int value) {
        NumberOfContentProviders numberOfContentProviders = new NumberOfContentProviders(paprikaApp, value);
        numberOfContentProviders.updateEntity();
        return numberOfContentProviders;
    }
}

