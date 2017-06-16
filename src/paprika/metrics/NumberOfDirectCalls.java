package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaMethod;

public class NumberOfDirectCalls extends UnaryMetric<Integer> {
    private NumberOfDirectCalls(PaprikaMethod paprikaMethod, int value) {
        this.value = value;
        this.entity = paprikaMethod;
        this.name = "number_of_direct_calls";
    }

    public static NumberOfDirectCalls createNumberOfDirectCalls(PaprikaMethod paprikaMethod, int value) {
        NumberOfDirectCalls numberOfDirectCalls = new NumberOfDirectCalls(paprikaMethod, value);
        numberOfDirectCalls.updateEntity();
        return numberOfDirectCalls;
    }
}

