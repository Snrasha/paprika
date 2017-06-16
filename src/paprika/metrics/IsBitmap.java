package paprika.metrics;

import paprika.entities.Entity;
import paprika.entities.PaprikaClass;

public class IsBitmap extends UnaryMetric<Boolean> {
    private IsBitmap(PaprikaClass paprikaClass, boolean value) {
        this.value = value;
        this.entity = paprikaClass;
        this.name = "is_bitmap";
    }

    public static IsBitmap createIsBitmap(PaprikaClass paprikaClass, boolean value) {
        IsBitmap isBitmap = new IsBitmap(paprikaClass, value);
        isBitmap.updateEntity();
        return isBitmap;
    }
}

