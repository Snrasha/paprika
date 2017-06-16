package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaClass;

public class LackofCohesionInMethods extends UnaryMetric<Integer> {
    private LackofCohesionInMethods(PaprikaClass paprikaClass) {
        this.value = paprikaClass.computeLCOM();
        this.entity = paprikaClass;
        this.name = "lack_of_cohesion_in_methods";
    }

    public static LackofCohesionInMethods createLackofCohesionInMethods(PaprikaClass paprikaClass) {
        LackofCohesionInMethods couplingBetweenObjects = new LackofCohesionInMethods(paprikaClass);
        couplingBetweenObjects.updateEntity();
        return couplingBetweenObjects;
    }
}

