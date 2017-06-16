package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaApp;

public class NumberOfViews extends UnaryMetric<Integer> {
    private NumberOfViews(PaprikaApp paprikaApp, int value) {
        this.value = value;
        this.entity = paprikaApp;
        this.name = "number_of_views";
    }

    public static NumberOfViews createNumberOfViews(PaprikaApp paprikaApp, int value) {
        NumberOfViews numberOfViews = new NumberOfViews(paprikaApp, value);
        numberOfViews.updateEntity();
        return numberOfViews;
    }
}

